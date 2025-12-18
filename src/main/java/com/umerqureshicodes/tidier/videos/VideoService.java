package com.umerqureshicodes.tidier.videos;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umerqureshicodes.tidier.Utilities.TwelveLabsResponse;
import com.umerqureshicodes.tidier.Utilities.TwelveLabsTaskResponse;
import com.umerqureshicodes.tidier.montages.Montage;
import jakarta.transaction.Transactional;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
public class VideoService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;
    @Value("${twelvelabs.index.id}")
    private String indexId;
    @Value("${project.path}")
    private String projectPath;
    private final VideoRepo videoRepo;

    @Autowired
    public VideoService(VideoRepo videoRepo) {
        this.videoRepo = videoRepo;
    }

    public  void convertToMp4(String inputPath, String outputPath) {
        // ffmpeg command
        String[] command = {
                "ffmpeg",
                "-i", inputPath,   // input file
                "-c:v", "libx264", // video codec
                "-c:a", "aac",     // audio codec
                "-strict", "experimental", // for AAC compatibility
                outputPath
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // merge stderr with stdout

        try {
            Process process = processBuilder.start();

            // capture output (ffmpeg logs progress here)
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                  //  System.out.println(line); // print ffmpeg output
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Conversion completed successfully!");
            } else {
                System.err.println("Conversion failed. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<VideoResponseDTO> uploadToDirectory(List<MultipartFile> files) {
        List<VideoResponseDTO> videoResponseDTOList = new ArrayList<>();
        try {
            // Save the files to the local file system
            for(MultipartFile file : files) {
                String filePath = projectPath+ "/uploads/" + file.getOriginalFilename();
                String fileName = file.getOriginalFilename();
                file.transferTo(new File(filePath));
                if (!Objects.requireNonNull(file.getContentType()).equalsIgnoreCase("video/mp4")){ // if different video file so .mov
                    int fileExtensionStart = file.getOriginalFilename().lastIndexOf(".");
                    String convertedFileName = file.getOriginalFilename().substring(0, fileExtensionStart) + ".mp4";
                    convertToMp4(filePath,projectPath+ "/uploads/" + convertedFileName );
                    new File(filePath).delete();
                    filePath =projectPath+ "/uploads/" + convertedFileName;
                    fileName = convertedFileName;
                }
               /*
                UPLOAD LATER, RIGHT NOW JUST IMPLEMENTING VIDEO LIBRARY COMPONENT

               Video uploadedVid = uploadToTwelveLabsAndSave(filePath, fileName );

                */
               // videoResponseDTOList.add(new VideoResponseDTO(uploadedVid.getName(),uploadedVid.getVideoId()));

                // BOTTOM THREE LINES ARE TEMP, SAVE DOESNT HAPPEN HERE

                String DUMMY_ID = "DUMMY ID: "+ UUID.randomUUID().toString().replace("-", "");
                videoRepo.save(new Video(DUMMY_ID,fileName ));
                videoResponseDTOList.add(new VideoResponseDTO(fileName ,DUMMY_ID));
            }
            return videoResponseDTOList;
        } catch (IOException e) {
            e.printStackTrace(); // Or use a logger instead
            System.out.println ("Failed to upload file: " + e.getMessage());
            return null;
        }
    }

    public Video uploadToTwelveLabsAndSave(String path,String filename) {

        HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/tasks")
                .header("x-api-key", apiKey)
                .field("index_id", indexId)
                .field("video_file", new File(path) )
                .asString();

        if(response.getStatus() == 200 || response.getStatus() == 201) {
            try{
                ObjectMapper mapper = new ObjectMapper();
                TwelveLabsTaskResponse twelvelabsTaskResponse = mapper.readValue(response.getBody(), TwelveLabsTaskResponse.class);
                Video video = new Video(twelvelabsTaskResponse.videoId(),filename);
                videoRepo.save(video);
                return video;
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Failed to upload file: " + e.getMessage());
                return null;
            }
        }
        else{
            System.out.println("API error: " + response.getStatus());
            return null;
        }
    }

    public List<VideoResponseDTO> getVideos() {
        List<VideoResponseDTO> videoResponseDTOList = new ArrayList<>();
        for (Video video : videoRepo.findAll()) {
            videoResponseDTOList.add(new VideoResponseDTO(video.getName(),video.getVideoId()));
        }
        return videoResponseDTOList;
    }

    public List<Video> getVideosByVideoIds(List<String> videoIds) {
        return  videoRepo.findAllByVideoIds(videoIds);
    }

    @Transactional
    public void updateVideo(Video video, Montage montage) {
        video.getMontages().add(montage);
    }

    public String deleteVideo(long id) {
        if (videoRepo.existsById(id)) {
            new File(projectPath + "/uploads/" + videoRepo.findById(id).get().getName()).delete();
            videoRepo.deleteById(id);
            return "Video deleted";
        }
        return "Video not found";
    }
}
