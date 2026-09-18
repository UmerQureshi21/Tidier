package com.umerqureshicodes.tidier.montages;


import com.umerqureshicodes.tidier.FFmpeg.FFmpegService;
import com.umerqureshicodes.tidier.TwelveLabs.TwelveLabsService;
import com.umerqureshicodes.tidier.TwelveLabs.TwelveLabsTimeStampResponse;
import com.umerqureshicodes.tidier.WebSocket.WebSocketServiceMessage;
import com.umerqureshicodes.tidier.embeddings.Embedding;
import com.umerqureshicodes.tidier.embeddings.EmbeddingService;
import com.umerqureshicodes.tidier.s3.S3Service;
import com.umerqureshicodes.tidier.users.AppUser;
import com.umerqureshicodes.tidier.users.UserRepo;
import com.umerqureshicodes.tidier.videos.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MontageService {

    private final FFmpegService fFmpegService;
    private final MontageRepo montageRepo;
    private final UserRepo userRepo;
    private final VideoService videoService;
    private final VideoRepo videoRepo;
    private final S3Service s3Service;
    private final TwelveLabsService twelveLabsService;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmbeddingService embeddingService;
    // Matches intervals like 00:04-00:08 in the TwelveLabs answer
    private static final Pattern INTERVAL_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{2})\\s*-\\s*(\\d{1,2}):(\\d{2})");

    // State for a single montage request. The service is shared by all requests, so this can't be stored in fields
    private static class MontageBuild {
        int duration = 0;
        final List<Video> videosUsed = new ArrayList<>();
        final List<Path> trimmedFiles = new ArrayList<>();
    }

    public MontageService(MontageRepo montageRepo, VideoService videoService, S3Service s3Service, TwelveLabsService twelveLabsService, SimpMessagingTemplate messagingTemplate, FFmpegService fFmpegService, UserRepo userRepo, VideoRepo videoRepo, EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
        this.montageRepo = montageRepo;
        this.videoService = videoService;
        this.s3Service = s3Service;
        this.twelveLabsService = twelveLabsService;
        this.messagingTemplate = messagingTemplate;
        this.fFmpegService = fFmpegService;
        this.userRepo = userRepo;
        this.videoRepo = videoRepo;
    }

    private void notify(String userEmail, String message, String montagePath) {
        // Only sent to the user who created the montage, they subscribe to /user/queue/montage-progress
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/montage-progress", new WebSocketServiceMessage(message, montagePath));
    }

    public MontageResponseDTO convertToDTO(Montage montage, String montageUrl) {
        List<VideoResponseDTO> videoResponseDTOs = new ArrayList<>();
        for (Video video: montage.getVideos()){
            videoResponseDTOs.add(new VideoResponseDTO(video.getName(),video.getVideoId(),video.isEmbedded()));
        }
        return new MontageResponseDTO(montage.getName(),videoResponseDTOs,montage.getPrompt(), montage.getCreatedAt(), montage.getDuration(),montageUrl, montage.isEmbedded());
    }

    public MontageResponseDTO createMontage(MontageRequestDTO montageRequestDTO, String email) {

        if(montageRepo.findAllByUserUsername(email).size() >= 10){
            throw new MontageCreationException("You've reached the limit of 10 montages.");
        }

        Optional<AppUser> user = userRepo.findByUsername(email) ;
        if(user.isEmpty()) {
            throw new MontageCreationException("User not found.");
        }

        // Only allow videos that belong to this user
        List<Video> selectedVideos = new ArrayList<>();
        for (VideoRequestDTO v : montageRequestDTO.videoRequestDTOs()) {
            Optional<Video> video = videoRepo.findByVideoIdAndUserUsername(v.getVideoId(), email);
            if (video.isEmpty()) {
                System.out.println("Video " + v.getVideoId() + " not found for user");
                throw new MontageCreationException("One of the selected videos could not be found.");
            }
            if (video.get().getAssetId() == null) {
                throw new MontageCreationException("\"" + video.get().getName()
                        + "\" is still being processed. Please try again in a minute.");
            }
            selectedVideos.add(video.get());
        }

        MontageBuild build = new MontageBuild();
        Map<Video, String> timestamps = analyzeVideoWithPrompt(selectedVideos, montageRequestDTO, email);
        if (timestamps.isEmpty()) {
            throw new MontageCreationException("Couldn't analyze the selected videos right now. Please try again later.");
        }
        // Unique key so montages with the same name (or users with similar emails) never overwrite each other
        String s3Key = "montages/" + user.get().getId() + "/" + UUID.randomUUID() + ".mp4";
        int ffmpegCode = 1;
        try {
            if (trimVideos(timestamps, build, email, montageRequestDTO.prompt())) {
                ffmpegCode = combineVideos(build.trimmedFiles, s3Key, email);
            }
        } finally {
            for (Path trimmedFile : build.trimmedFiles) {
                deleteQuietly(trimmedFile);
            }
        }

        if(ffmpegCode != 0) {
            System.out.println("ERROR EXIT CODE: "+ffmpegCode);
            throw new MontageCreationException("Something went wrong while creating the montage. Please try again.");
        }

        Montage montage = new Montage(montageRequestDTO.name(),montageRequestDTO.prompt(), user.get(), build.duration);
        montage.setS3Key(s3Key);
        // Only the videos that clips were actually taken from, this also adds the montage to each video
        montage.setVideos(build.videosUsed);
        Montage savedMontage = montageRepo.save(montage);

        // Make the montage searchable. If this fails the background job retries it
        if (embeddingService.embedAndStore(Embedding.Kind.MONTAGE, savedMontage.getId(), user.get().getId(),
                buildMontageText(savedMontage))) {
            savedMontage.setEmbedded(true);
            savedMontage = montageRepo.save(savedMontage);
        }

        String preSignedUrl = s3Service.generatePresignedGetUrl("tidier", s3Key).toString();
        notify(email, montageRequestDTO.name() +" created!", preSignedUrl);
        System.out.println(montageRequestDTO.name() +" created!");
        return convertToDTO(savedMontage, preSignedUrl);
    }

    //https://docs.twelvelabs.io/v1.3/api-reference/analyze-videos/analyze
    // Returns the raw interval text for each video that TwelveLabs answered for
    public Map<Video, String> analyzeVideoWithPrompt(List<Video> videos, MontageRequestDTO montageRequestDTO, String email) {
        Map<Video, String> timestamps = new LinkedHashMap<>();
        for(Video video : videos) {
            TwelveLabsTimeStampResponse response = twelveLabsService.getIntervalsOfTopic(video.getAssetId(), montageRequestDTO.sentence());
            if (response != null && response.data() != null) {
                timestamps.put(video, response.data());
                notify(email, "Successfully extracted " + montageRequestDTO.prompt() + " from " + video.getName(), null);
            }
        }
        return timestamps;
    }

    // Trims every interval into its own temp file (added to build.trimmedFiles), returns false if something failed
    // and throws if none of the videos contain the topic
    private boolean trimVideos(Map<Video, String> timestamps, MontageBuild build, String userEmail, String topic) {
        for (Map.Entry<Video, String> entry : timestamps.entrySet()) {
            Video video = entry.getKey();
            // A video with no instance of the topic comes back as 00:00-00:00, which is skipped here
            List<int[]> intervals = new ArrayList<>();
            Matcher matcher = INTERVAL_PATTERN.matcher(entry.getValue());
            while (matcher.find()) {
                int start = Integer.parseInt(matcher.group(1)) * 60 + Integer.parseInt(matcher.group(2));
                int end = Integer.parseInt(matcher.group(3)) * 60 + Integer.parseInt(matcher.group(4));
                if (end > start) {
                    intervals.add(new int[]{start, end});
                }
            }
            if (intervals.isEmpty()) {
                System.out.println("No time stamp found in " + video.getName());
                continue;
            }

            File inputTempFile = null;
            try {
                // Download each source video once, not once per interval
                URL videoUrl = s3Service.generatePresignedGetUrl("tidier", videoService.getS3Name(video));
                inputTempFile = downloadPresignedUrlToTempFile(videoUrl.toString());
                for (int[] interval : intervals) {
                    System.out.println(interval[0] + " " + interval[1] + " " + video.getName());
                    Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), "trimmed-" + UUID.randomUUID() + ".mp4");
                    build.trimmedFiles.add(tempPath);
                    int exitCode = fFmpegService.trimVideo(inputTempFile.getAbsolutePath(), tempPath.toString(),
                            String.valueOf(interval[0]), String.valueOf(interval[1]));
                    System.out.println("FFmpeg finished with exit code " + exitCode);
                    if (exitCode != 0) {
                        return false;
                    }
                    build.duration += interval[1] - interval[0];
                }
                build.videosUsed.add(video);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR WAS CAUGHT: \n" + e.getMessage());
                return false;
            } finally {
                if (inputTempFile != null) {
                    deleteQuietly(inputTempFile.toPath());
                }
            }
        }

        if (build.trimmedFiles.isEmpty()) {
            throw new MontageCreationException("No moments matching \"" + topic + "\" were found in the selected videos.");
        }
        notify(userEmail, "Finished trimming videos...", null);
        return true;
    }

    public int combineVideos(List<Path> trimmedFiles, String s3Key, String userEmail) {
        notify(userEmail, "Combining videos...", null);
        int exitCode = 100;
        File concatFile = null;
        Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), "montage-" + UUID.randomUUID() + ".mp4");
        try {
            concatFile = Files.createTempFile("videos-", ".txt").toFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(concatFile))) {
                for (Path trimmedFile : trimmedFiles) {
                    writer.write("file '" + trimmedFile.toString() + "'\n");
                }
            }
            exitCode = fFmpegService.combineVideo(concatFile.getAbsolutePath(),tempPath.toString());
            if (exitCode == 0) {
                s3Service.putObject("tidier", s3Key, tempPath.toFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            exitCode = 100;
        } finally {
            if (concatFile != null) {
                deleteQuietly(concatFile.toPath());
            }
            deleteQuietly(tempPath);
        }
        return exitCode;
    }

    // What gets embedded for a montage: the user's own words plus whatever the source videos show
    public String buildMontageText(Montage montage) {
        StringBuilder text = new StringBuilder(montage.getName() + ". " + montage.getPrompt() + ".");
        for (Video video : montage.getVideos()) {
            if (video.getSummary() != null) {
                text.append(" ").append(video.getSummary());
            }
        }
        return text.toString();
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.out.println("Failed to delete temp file " + path + ": " + e.getMessage());
        }
    }

    public List<MontageResponseDTO> getMontages(String userEmail) {
        List<MontageResponseDTO> montageResponseDTOs = new ArrayList<>();
        List<Montage> montages = montageRepo.findAllByUserUsername(userEmail) ;
        for (Montage montage : montages) {
            List<VideoResponseDTO> videoResponseDTOs = new ArrayList<>();
            List<Video> videos = montage.getVideos();
            for (Video video : videos) {
                String videoPreviewUrl = videoService.getVideoUrl(videoService.getS3Name(video));
                videoResponseDTOs.add(new VideoResponseDTO(video.getName(),video.getVideoId(),videoPreviewUrl,video.isEmbedded()));
            }
            String preSignedUrl = s3Service.generatePresignedGetUrl("tidier",getS3Key(montage, userEmail)).toString();
            montageResponseDTOs.add(new MontageResponseDTO(montage.getName(),videoResponseDTOs,montage.getPrompt(),montage.getCreatedAt(),montage.getDuration(),preSignedUrl,montage.isEmbedded()));
        }
        return montageResponseDTOs;
    }

    public String getS3Key(Montage montage, String userEmail) {
        if (montage.getS3Key() != null) {
            return montage.getS3Key();
        }
        // Montages created before s3Key was stored used this path
        return "montages/" + userEmail.split("@")[0] + "/" + montage.getName() + ".mp4";
    }

    @Transactional
    public String deleteMontage(Long montageId, String userEmail) {
        // Only finds the montage if it belongs to this user, so users can't delete each other's montages
        Optional<Montage> montageOptional = montageRepo.findByIdAndUserUsername(montageId, userEmail);
        if (montageOptional.isEmpty()) {
            return "Montage not found";
        }
        Montage montage = montageOptional.get();
        montageRepo.delete(montage);
        embeddingService.deleteFor(Embedding.Kind.MONTAGE, montage.getId());

        // Best effort cleanup, a failure here shouldn't undo the db delete
        try {
            s3Service.deleteObject("tidier", getS3Key(montage, userEmail));
        } catch (Exception e) {
            System.out.println("Failed to delete montage from S3: " + e.getMessage());
        }
        return "Successfully deleted montage with id:" + montageId;
    }

    public File downloadPresignedUrlToTempFile(String presignedUrl) throws IOException {

        File tempFile = Files.createTempFile("s3-video-", ".mp4").toFile();

        URI uri = URI.create(presignedUrl);
        URL url = uri.toURL();

        try (
                InputStream in = url.openStream();
                OutputStream out = new FileOutputStream(tempFile)
        ) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }

    public MontageResponseDTO getLongestMontage(String userEmail) {
        List<MontageResponseDTO> montages = this.getMontages(userEmail);
        if(montages.isEmpty()) {
            return null;
        }
        int max = montages.getFirst().duration();
        int maxIndex = 0;
        for (int i = 1; i < montages.size(); i++) {
            if (montages.get(i).duration() > max) {
                max = montages.get(i).duration();
                maxIndex = i;
            }
        }
        return montages.get(maxIndex);
    }

    public MontageResponseDTO getMostVideoMontage(String userEmail) {
        List<MontageResponseDTO> montages = this.getMontages(userEmail);
        if(montages.isEmpty()) {
            return null;
        }
        int max = montages.getFirst().videos().size();
        int maxIndex = 0;
        for (int i = 1; i < montages.size(); i++) {
            int count = montages.get(i).videos().size();
            if (count > max) {
                max = count ;
                maxIndex = i;
            }
        }
        return montages.get(maxIndex);
    }
}
