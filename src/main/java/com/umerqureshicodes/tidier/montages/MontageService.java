package com.umerqureshicodes.tidier.montages;


import com.umerqureshicodes.tidier.Utilities.TwelveLabsTimeStampResponse;
import com.umerqureshicodes.tidier.Utilities.WebSocketServiceMessage;
import com.umerqureshicodes.tidier.s3.S3Service;
import com.umerqureshicodes.tidier.videos.*;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class MontageService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;
    @Value("${project.path}")
    private String projectPath;
    @Value("${backend.host}")
    private String backendHost;
    private final MontageRepo montageRepo;
    private final VideoService videoService;
    private final S3Service s3Service;
    private final SimpMessagingTemplate messagingTemplate;
    public MontageService(MontageRepo montageRepo, VideoService videoService, S3Service s3Service, SimpMessagingTemplate messagingTemplate) {
        this.montageRepo = montageRepo;
        this.videoService = videoService;
        this.s3Service = s3Service;
        this.messagingTemplate = messagingTemplate;
    }

//    private void notify(String message, String montagePath) {
//        // Push message to all clients subscribed to /topic/montage-progress
//        messagingTemplate.convertAndSend("/topic/montage-progress", new WebSocketServiceMessage(message, montagePath));
//    }


    public MontageResponseDTO convertToDTO(Montage montage, String montageUrl) {
        List<VideoResponseDTO> videoResponseDTOs = new ArrayList<>();
        for (Video video: montage.getVideos()){
            videoResponseDTOs.add(new VideoResponseDTO(video.getVideoId(),video.getName()));
        }
        return new MontageResponseDTO(montage.getName(),videoResponseDTOs,montage.getPrompt(),null);
    }

    public MontageResponseDTO createMontage(MontageRequestDTO montageRequestDTO) {
        Montage montage = new Montage(montageRequestDTO.name(),montageRequestDTO.prompt());
       int ffmpegCode = combineVideos(trimVideos(analyzeVideoWithPrompt(montageRequestDTO),montageRequestDTO.videoRequestDTOs()),montageRequestDTO.name());
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
           //notify(montageRequestDTO.name() +" created!",  "/montages/"+montageRequestDTO.name());
           // add topic to send montage path to tsx component
           String preSignedUrl = s3Service.generatePresignedGetUrl("bucket","montages/"+montageRequestDTO.name()+".mp4").toString() ;
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
            String requestBody = "{"
                    + "\"video_id\": \"" + v.getVideoId() + "\","
                    + "\"prompt\": \"" + montageRequestDTO.sentence() + "\","
                    + "\"temperature\": 0.2,"
                    + "\"stream\": false"
                    + "}";
            // montageRequestDTO.sentence() is gonna be "Give me all the timestamps of ___ in this format: 00:00-00:04, 00:04-00:08, 00:11-00:13
            HttpResponse<TwelveLabsTimeStampResponse> response =
                    Unirest.post("https://api.twelvelabs.io/v1.3/analyze")
                            .header("x-api-key", apiKey)
                            .header("Content-Type", "application/json")
                            .body(requestBody)
                            .asObject(TwelveLabsTimeStampResponse.class);

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                timestamps.add(response.getBody().data());
                //notify("Successfully extracted " + montageRequestDTO.prompt() + " from " + v.getName(), null);
            }

        }
        return timestamps;
    }

    public List<String> trimVideos(List<String> timeStamps, List<VideoRequestDTO> videoRequestDTOs) {
        List<HashMap<String,String>> intervals = new ArrayList<>();
        List<String> trimmedVideosToCombine = new ArrayList<>();
        int emptyTimeStampCount = 0;

        //timestamps in format of [[00:00-00:04, 00:04-00:08, 00:11-00:13],[00:01-00:03, 00:10-00:12]]
        for (int i = 0; i < timeStamps.size(); i++) {
            for(String timeStamp : timeStamps.get(i).split(", ")) {
                    HashMap<String,String> interval = new HashMap<>();
                    String[] times = timeStamp.split("-");
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
                    URL videoUrl = s3Service.generatePresignedGetUrl("tidier", "test/" + interval.get("video"));
                    System.out.println(videoUrl.toString());
                    File inputTempFile = downloadPresignedUrlToTempFile(videoUrl.toString());
                    String trimmedVideoName = interval.get("video") + "-trimmed-" + UUID.randomUUID().toString() + ".mp4";
                    String outputPath = projectPath + "/trimmed-uploads/" + trimmedVideoName;
                    trimmedVideosToCombine.add(trimmedVideoName);

                    //Problem is that I'm pretty sure the local version ffmpeg creates the file, but for the cloud one i made the file before which didnt work
                    Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), trimmedVideoName);

                    ProcessBuilder pb = new ProcessBuilder(
                            "ffmpeg",
                            "-y", // overwrite if I need too
                            "-i", inputTempFile.getAbsolutePath(),
                            "-ss", interval.get("start"),
                            "-to", interval.get("end"),
                            "-c:v", "libx264",
                            "-crf", "23",
                            "-preset", "fast",
                            "-c:a", "aac",
                            "-b:a", "192k",
                            tempPath.toString()
                    );

                    pb.redirectErrorStream(true); // merge stderr into stdout
                    Process process = pb.start();

                    // Capture output (optional, for debugging)
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    int exitCode = process.waitFor();
                    System.out.println("FFmpeg finished with exit code " + exitCode);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR WAS CAUGHT: \n" + e.getMessage());
                    return null;
                }
            }
            else{
                System.out.println("No time stamp found in" + interval.get("video"));
                emptyTimeStampCount++;
            }
            i++;
        }
        //notify("Finished trimming videos...", null);
        return trimmedVideosToCombine;
    }

    public int combineVideos(List<String> trimmedFiles, String outputFileName) {
        //notify("Combining videos...", null);


        String tempDir = System.getProperty("java.io.tmpdir");
        if(trimmedFiles == null) {
            return 1;
        }

        int exitCode = 100;
        try {
            // 1. Create text file to list trimmed video paths and output file to place montage in
            File concatFile = Files.createTempFile("videos-", ".txt").toFile();
            Path tempPath = Paths.get(tempDir ,outputFileName+".mp4");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(concatFile))) {
                for (String file : trimmedFiles) {
                    writer.write("file '"+tempDir + file + "'\n");
                }
            }
            // 2. Run ffmpeg with re-encoding for compatibility with mac's finder
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg","-y", "-f", "concat", "-safe", "0",
                    "-i", concatFile.getAbsolutePath(),
                    "-c:v", "libx264", "-crf", "23", "-preset", "veryfast",
                    "-c:a", "aac",
                    tempPath.toString()
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();
            // Log output in case of errors
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            exitCode = process.waitFor();
            if (exitCode == 0) {
                for (String file : trimmedFiles) {
                    Files.deleteIfExists(Paths.get(file));
                }
                File montageFile = tempPath.toFile();
                Files.deleteIfExists(Paths.get(concatFile.getAbsolutePath()));
                s3Service.putObject("tidier","montages/"+outputFileName+".mp4", montageFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error: " + e.getMessage());
        }
        return exitCode;
    }

    public List<MontageResponseDTO> getMontages() {
        List<MontageResponseDTO> montageResponseDTOs = new ArrayList<>();
        List<Montage> montages = montageRepo.findAll();
        for (Montage montage : montages) {
            List<VideoResponseDTO> videoResponseDTOs = new ArrayList<>();
            List<Video> videos = montage.getVideos();
            for (Video video : videos) {
                videoResponseDTOs.add(new VideoResponseDTO(video.getName(),video.getVideoId()));
            }
            String preSignedUrl = s3Service.generatePresignedGetUrl("tidier","montages/"+montage.getName()).toString();
            montageResponseDTOs.add(new MontageResponseDTO(montage.getName(),videoResponseDTOs,montage.getPrompt(),preSignedUrl));
        }
        return montageResponseDTOs;
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
