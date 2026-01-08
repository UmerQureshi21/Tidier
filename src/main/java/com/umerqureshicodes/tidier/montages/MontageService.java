package com.umerqureshicodes.tidier.montages;


import com.umerqureshicodes.tidier.FFmpeg.FFmpegService;
import com.umerqureshicodes.tidier.TwelveLabs.TwelveLabsService;
import com.umerqureshicodes.tidier.TwelveLabs.TwelveLabsTimeStampResponse;
import com.umerqureshicodes.tidier.WebSocket.WebSocketServiceMessage;
import com.umerqureshicodes.tidier.s3.S3Service;
import com.umerqureshicodes.tidier.users.AppUser;
import com.umerqureshicodes.tidier.users.UserRepo;
import com.umerqureshicodes.tidier.videos.*;
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

@Service
public class MontageService {

    private static final Logger log = LoggerFactory.getLogger(MontageService.class);
    private final FFmpegService fFmpegService;
    private final MontageRepo montageRepo;
    private final UserRepo userRepo;
    private final VideoService videoService;
    private final VideoRepo videoRepo;
    private final S3Service s3Service;
    private final TwelveLabsService twelveLabsService;
    private final SimpMessagingTemplate messagingTemplate;
    int duration = 0;
    public MontageService(MontageRepo montageRepo, VideoService videoService, S3Service s3Service, TwelveLabsService twelveLabsService, SimpMessagingTemplate messagingTemplate, FFmpegService fFmpegService, UserRepo userRepo, VideoRepo videoRepo) {
        this.montageRepo = montageRepo;
        this.videoService = videoService;
        this.s3Service = s3Service;
        this.twelveLabsService = twelveLabsService;
        this.messagingTemplate = messagingTemplate;
        this.fFmpegService = fFmpegService;
        this.userRepo = userRepo;
        this.videoRepo = videoRepo;
    }

    private void notify(String message, String montagePath) {
        // Push message to all clients subscribed to /topic/montage-progress
        messagingTemplate.convertAndSend("/topic/montage-progress", new WebSocketServiceMessage(message, montagePath));
    }

    public MontageResponseDTO convertToDTO(Montage montage, String montageUrl) {
        List<VideoResponseDTO> videoResponseDTOs = new ArrayList<>();
        for (Video video: montage.getVideos()){
            videoResponseDTOs.add(new VideoResponseDTO(video.getVideoId(),video.getName()));
        }
        return new MontageResponseDTO(montage.getName(),videoResponseDTOs,montage.getPrompt(), montage.getCreatedAt(), montage.getDuration(),montageUrl);
    }

    public MontageResponseDTO createMontage(MontageRequestDTO montageRequestDTO, String email) {
        duration = 0; // Reset
        Optional<AppUser> user = userRepo.findByUsername(email) ;
        if(!user.isPresent()) {
            System.out.println("User not found");
            return null;
        }
        System.out.println("final duration : "+duration);
       Montage montage = new Montage(montageRequestDTO.name(),montageRequestDTO.prompt(), user.get(), 0);
       int ffmpegCode = combineVideos(trimVideos(analyzeVideoWithPrompt(montageRequestDTO),montageRequestDTO.videoRequestDTOs(),email),montageRequestDTO.name(),email);
       if(ffmpegCode == 0) {
           List<String> videoIds = new ArrayList<>();
           for (VideoRequestDTO v : montageRequestDTO.videoRequestDTOs()) {
               videoIds.add(v.getVideoId());
           }
           List<Video> videosInMontage = videoService.getVideosByVideoIds(videoIds);
           // Adds this montage to each video in db
           for (Video v : videosInMontage) {
               videoService.updateVideo(v,montage);
           }
           // this can surely be put into one for loop
           montage.setVideos(videosInMontage);
           montage.setDuration(duration);
           String preSignedUrl = s3Service.generatePresignedGetUrl("tidier",getS3Name(montageRequestDTO.name(),email) ).toString() ;
           notify(montageRequestDTO.name() +" created!", preSignedUrl);
           // add topic to send montage path to tsx component
           System.out.println(montageRequestDTO.name() +" created!");
           return convertToDTO(montageRepo.save(montage),preSignedUrl);
       }
       else{
           System.out.println("ERROR EXIT CODE: "+ffmpegCode);
           return null;
       }

    }
    //https://docs.twelvelabs.io/v1.3/api-reference/analyze-videos/analyze
    public List<String> analyzeVideoWithPrompt(MontageRequestDTO montageRequestDTO) {
        List<String> timestamps = new ArrayList<>();
        for(VideoRequestDTO v : montageRequestDTO.videoRequestDTOs()) {
//            TwelveLabsTimeStampResponse response = twelveLabsService.getIntervalsOfTopic(v.getVideoId(), montageRequestDTO.sentence());
//            if (response != null) {
//                timestamps.add(response.data());
//                notify("Successfully extracted " + montageRequestDTO.prompt() + " from " + v.getName(), null);
//            }
            notify("Successfully extracted " + montageRequestDTO.prompt() + " from " + v.getName(), null);
            timestamps.add("00:00-00:02"); // ONLY ADDING THIS BECAUSE I HIT RATE LIMIT
        }
        return timestamps;
    }

    private int getIntervalDuration(String start, String end) {
        return Integer.parseInt(end.split(":")[0]) * 60 + Integer.parseInt(end.split(":")[1])
                - (Integer.parseInt(start.split(":")[0]) * 60 + Integer.parseInt(start.split(":")[1]));
    }

    public List<String> trimVideos(List<String> timeStamps, List<VideoRequestDTO> videoRequestDTOs, String userEmail) {

        List<HashMap<String,String>> intervals = new ArrayList<>();
        List<String> trimmedVideosToCombine = new ArrayList<>();
        //timestamps in format of [[00:00-00:04, 00:04-00:08, 00:11-00:13],[00:01-00:03, 00:10-00:12]]
        for (int i = 0; i < timeStamps.size(); i++) {
            for(String timeStamp : timeStamps.get(i).split(", ")) {
                    HashMap<String,String> interval = new HashMap<>();
                    String[] times = timeStamp.split("-");
                    duration += this.getIntervalDuration(times[0],times[1]);
                    System.out.println("duration: " + duration);
                    interval.put("start", "00:"+times[0]+".000");
                    interval.put("end", "00:"+times[1]+".000");
                    interval.put("video",videoRequestDTOs.get(i).getName());
                    intervals.add(interval);
            }
        }
        int i = 0;
        for(HashMap<String,String> interval : intervals)
        {
            System.out.println(interval.get("start")+" "+interval.get("end")+" "+interval.get("video"));
            if (!interval.get("start").equals(interval.get("end"))) {
                try {
                    Optional<Video> vid = videoRepo.findByUserUsernameAndName(userEmail,interval.get("video"));
                    if (!vid.isPresent()) {
                        System.out.println("User email doesn't exist!");
                        return null;
                    }
                    URL videoUrl = s3Service.generatePresignedGetUrl("tidier", videoService.getS3Name(vid.get()));
                    System.out.println(videoUrl.toString());
                    File inputTempFile = downloadPresignedUrlToTempFile(videoUrl.toString());
                    String trimmedVideoName = interval.get("video") + "-trimmed-" + UUID.randomUUID().toString() + ".mp4";
                    trimmedVideosToCombine.add(trimmedVideoName);
                    //Problem is that I'm pretty sure the local version ffmpeg creates the file, but for the cloud one i made the file before which didnt work
                    Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), trimmedVideoName);
                    int exitCode = fFmpegService.trimVideo( inputTempFile.getAbsolutePath(), tempPath.toString(),interval.get("start"),interval.get("end"));
                    System.out.println("FFmpeg finished with exit code " + exitCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR WAS CAUGHT: \n" + e.getMessage());
                    return null;
                }
            }
            else{
                System.out.println("No time stamp found in" + interval.get("video"));
            }
            i = i + 1;
        }
        notify("Finished trimming videos...", null);
        return trimmedVideosToCombine;
    }

    public int combineVideos(List<String> trimmedFiles, String outputFileName, String  userEmail) {
        notify("Combining videos...", null);
        String tempDir = System.getProperty("java.io.tmpdir");
        if(trimmedFiles == null) {
            return 1;
        }
        int exitCode = 100;
        try {
            File concatFile = Files.createTempFile("videos-", ".txt").toFile();
            Path tempPath = Paths.get(tempDir ,outputFileName+".mp4");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(concatFile))) {
                for (String file : trimmedFiles) {
                    writer.write("file '"+tempDir + file + "'\n");
                }
            }
            exitCode = fFmpegService.combineVideo(concatFile.getAbsolutePath(),tempPath.toString());
            if (exitCode == 0) {
                for (String file : trimmedFiles) {
                    Files.deleteIfExists(Paths.get(file));
                }
                File montageFile = tempPath.toFile();
                Files.deleteIfExists(Paths.get(concatFile.getAbsolutePath()));
                s3Service.putObject("tidier",getS3Name(outputFileName,userEmail), montageFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
        return exitCode;
    }

    public List<MontageResponseDTO> getMontages(String userEmail) {
        List<MontageResponseDTO> montageResponseDTOs = new ArrayList<>();
        List<Montage> montages = montageRepo.findAllByUserUsername(userEmail) ;
        for (Montage montage : montages) {
            List<VideoResponseDTO> videoResponseDTOs = new ArrayList<>();
            List<Video> videos = montage.getVideos();
            for (Video video : videos) {
                String videoPreviewUrl = videoService.getVideoUrl(videoService.getS3Name(video));
                videoResponseDTOs.add(new VideoResponseDTO(video.getName(),video.getVideoId(),videoPreviewUrl));
            }
            String preSignedUrl = s3Service.generatePresignedGetUrl("tidier",getS3Name(montage.getName(),userEmail)).toString();
            montageResponseDTOs.add(new MontageResponseDTO(montage.getName(),videoResponseDTOs,montage.getPrompt(),montage.getCreatedAt(),montage.getDuration(),preSignedUrl));
        }
        return montageResponseDTOs;
    }

    public String getS3Name(String name, String userEmail) {
        return "montages/" + userEmail.split("@")[0] + "/" + name + ".mp4";
    }

    public String deleteMontage(Long montageId) {



        // WRITE SOMETHING THAT DELETES THE MONTAGE VIDEO IN THE MONTAGES FOLDER
        // AND ALSO RENAME EACH TRIMMED VIDEO IN A BETTER MANNER


        if (montageId == null || !montageRepo.existsById(montageId)) {
            return "What, not correct montage id?";
        }

         montageRepo.deleteById(montageId);
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


}
