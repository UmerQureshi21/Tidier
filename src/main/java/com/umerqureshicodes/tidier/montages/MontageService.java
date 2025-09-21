package com.umerqureshicodes.tidier.montages;


import com.umerqureshicodes.tidier.Utilities.TwelveLabsTimeStampResponse;
import com.umerqureshicodes.tidier.Utilities.WebSocketServiceMessage;
import com.umerqureshicodes.tidier.videos.*;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class MontageService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;
    @Value("${project.path}")
    private String projectPath;
    private final MontageRepo montageRepo;
    private final VideoService videoService;
    private final SimpMessagingTemplate messagingTemplate;
    public MontageService(MontageRepo montageRepo, VideoService videoService, SimpMessagingTemplate messagingTemplate) {
        this.montageRepo = montageRepo;
        this.videoService = videoService;
        this.messagingTemplate = messagingTemplate;
    }

    private void notify(String message) {
        // Push message to all clients subscribed to /topic/montage-progress
        messagingTemplate.convertAndSend("/topic/montage-progress", new WebSocketServiceMessage(message));
    }

    private void deleteTrimmedUploads(){
        File dir = new File(projectPath+"/trimmed-uploads");

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if (file.delete()) {
                            System.out.println("Deleted: " + file.getName());
                        } else {
                            System.out.println("Failed to delete: " + file.getName());
                        }
                    }
                }
            }
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }
    }

    public MontageResponseDTO convertToDTO(Montage montage) {
        List<VideoResponseDTO> videoResponseDTOs = new ArrayList<>();
        for (Video video: montage.getVideos()){
            videoResponseDTOs.add(new VideoResponseDTO(video.getVideoId(),video.getName()));
        }
        return new MontageResponseDTO(montage.getName(),videoResponseDTOs,montage.getPrompt());
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
           for (Video v : videosInMontage) {
               videoService.updateVideo(v,montage);
           }
           // this can surely be put into one for loop
           montage.setVideos(videosInMontage);
           deleteTrimmedUploads();
           return convertToDTO(montageRepo.save(montage));
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
            HttpResponse<TwelveLabsTimeStampResponse> response =
                    Unirest.post("https://api.twelvelabs.io/v1.3/analyze")
                            .header("x-api-key", apiKey)
                            .header("Content-Type", "application/json")
                            .body(requestBody)
                            .asObject(TwelveLabsTimeStampResponse.class);

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                timestamps.add(response.getBody().data());
                notify("Successfully extracted " + montageRequestDTO.prompt() + " from video " + v.getName());
            }

        }
        return timestamps;
    }

    public List<String> trimVideos(List<String> timeStamps, List<VideoRequestDTO> videoRequestDTOs) {
        List<HashMap<String,String>> intervals = new ArrayList<>();
        List<String> trimmedVideosToCombine = new ArrayList<>();

        //timestamps in format of [[00:00-00:04, 00:04-00:08, 00:11-00:13],[00:01-00:03, 00:10-00:12]]
        for (int i = 0; i < timeStamps.size(); i++) {

            // POSSIBILITY OF TIMESTAMPS BEING NONE I THINK
            for(String timeStamp : timeStamps.get(i).split(", ")) {
                if (!timeStamp.isEmpty()){
                    HashMap<String,String> interval = new HashMap<>();
                    String[] times = timeStamp.split("-");
                    interval.put("start", "00:"+times[0]+".000");
                    interval.put("end", "00:"+times[1]+".000");
                    interval.put("video",videoRequestDTOs.get(i).getName());
                    System.out.println(interval.get("start")+" "+interval.get("end")+" "+interval.get("video"));
                    intervals.add(interval);
                }
                else{
                    System.out.println("\n\n\n\nNo time stamp found of montage request!!!\n\n\n\n");
                }

            }
        }
        int i = 0;
        for(HashMap<String,String> interval : intervals)
        {
            try {
                String trimmedVideoName = interval.get("video")+"-trimmed-"+i+".mp4";
                String inputPath =projectPath+ "/uploads/"+interval.get("video");
                String outputPath = projectPath + "/trimmed-uploads/"+trimmedVideoName;
                trimmedVideosToCombine.add(trimmedVideoName);
                System.out.println("\n\n\n\n"+trimmedVideoName+"\n\n\n\n");

                ProcessBuilder pb = new ProcessBuilder(
                        "ffmpeg",
                        "-i", inputPath,
                        "-ss", interval.get("start"),
                        "-to", interval.get("end"),
                        "-c:v", "libx264",
                        "-crf", "23",
                        "-preset", "fast",
                        "-c:a", "aac",
                        "-b:a", "192k",
                        outputPath
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
                System.out.println(e.getMessage());
                return null;
            }
            i++;
        }
        notify("Finished trimming videos...");
        return trimmedVideosToCombine;
    }

    public int combineVideos(List<String> trimmedFiles, String outputFileName) {

        if(trimmedFiles == null) {
            return 1;
        }
        int exitCode = 100;
        try {
            // 1. Create videos.txt
            File listFile = new File("videos.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile))) {
                for (String file : trimmedFiles) {
                    writer.write("file 'trimmed-uploads/" + file + "'\n");
                    System.out.println("File supposed to go in videos.txt: " + file);
                }
            }



            // 2. Run ffmpeg with re-encoding for compatibility with mac's finder
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-f", "concat", "-safe", "0",
                    "-i", "videos.txt",
                    "-c:v", "libx264", "-crf", "23", "-preset", "veryfast",
                    "-c:a", "aac",
                    outputFileName
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Log output in case of errors
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            exitCode = process.waitFor();
            if (exitCode == 0) {
                Path projectRoot = Path.of(System.getProperty("user.dir")); //returns the directory where the Java process was started (usually your project root when you run the app)
                Path source = projectRoot.resolve(outputFileName);
                Path target = projectRoot.resolve("frontend/public/montages/"+outputFileName);

                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                listFile.delete();


            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error: " + e.getMessage());
        }
        notify("Finished combining videos...");
        return exitCode;
    }

    public List<String> getMontages() {


        // SHOULD BE CAHGNED TO REPO . FINDALL AND THEN YOU PREPEND /montages/
        List<String> filesList = new ArrayList<>();
        Path montagesPath = Paths.get(System.getProperty("user.dir"), "frontend", "public", "montages");


        try (DirectoryStream<Path> stream = Files.newDirectoryStream(montagesPath)) {
            for (Path path : stream) {
                File file = path.toFile();
                if (file.isFile()) {
                    filesList.add("/montages/" + file.getName());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
            // optionally return an empty list if there's an error
        }

        return filesList;
    }

    public String deleteMontage(Long montageId) {



        // WRITE SOMETHING THAT DELETES THE MONTAGE VIDEO IN THE MONTAGES FOLDER
        // AND ALSO RENAME EACH TRIMMED VIDEO IN A BETTER MANNER




         montageRepo.deleteById(montageId);
         return "Successfully deleted montage with id:" + montageId;
    }
}
