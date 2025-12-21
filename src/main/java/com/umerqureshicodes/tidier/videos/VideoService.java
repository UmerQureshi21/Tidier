package com.umerqureshicodes.tidier.videos;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umerqureshicodes.tidier.Utilities.TwelveLabsTaskResponse;
import com.umerqureshicodes.tidier.montages.Montage;
import com.umerqureshicodes.tidier.s3.S3Service;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

@Service
public class VideoService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;
    @Value("${twelvelabs.index.id}")
    private String indexId;
    @Value("${project.path}")
    private String projectPath;
    private final VideoRepo videoRepo;
    private final S3Service s3Service;

    @Autowired
    public VideoService(VideoRepo videoRepo, S3Service s3Service) {
        this.videoRepo = videoRepo;
        this.s3Service = s3Service;
    }

    public File convertToMp4(MultipartFile multipartFile) throws IOException, InterruptedException {

        // 1. Create temp input file (original format)
        File inputTempFile = Files.createTempFile("upload-", "-" + multipartFile.getOriginalFilename()).toFile();
        multipartFile.transferTo(inputTempFile);

        // 2. Create temp output file (mp4)
        File outputTempFile = Files.createTempFile("converted-", ".mp4").toFile();

        // 3. FFmpeg command
        String[] command = {
                "ffmpeg",
                "-y", // overwrite if exists
                "-i", inputTempFile.getAbsolutePath(),
                "-c:v", "libx264",
                "-c:a", "aac",
                outputTempFile.getAbsolutePath()
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // 4. Consume FFmpeg output (important to avoid deadlocks)
        try (var reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null) {
                // optionally log ffmpeg output
            }
        }

        int exitCode = process.waitFor();

        // 5. Cleanup input temp file
        inputTempFile.delete();

        if (exitCode != 0) {
            outputTempFile.delete();
            throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
        }

        // 6. Return MP4 temp file (caller must delete after use)
        return outputTempFile;
    }

    public List<VideoResponseDTO> save(List<MultipartFile> files) {
        List<VideoResponseDTO> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            File tempFile = null;

            try {
                if (!"video/mp4".equalsIgnoreCase(file.getContentType())) {
                    tempFile = convertToMp4(file);
                } else {
                    tempFile = Files.createTempFile("upload-", ".mp4").toFile();
                    file.transferTo(tempFile); //after transferTo, the multipart is designed to no longer be used
                }

                // Upload to TwelveLabs
                Video uploadedVid = uploadToTwelveLabsAndSave(
                        tempFile,
                        file.getOriginalFilename()
                );

                // Upload to S3 USING FILE
                s3Service.putObject(
                        "tidier",
                        "test/" + file.getOriginalFilename(),
                        tempFile
                );

                responses.add(
                        new VideoResponseDTO(
                                uploadedVid.getName(),
                                uploadedVid.getVideoId()
                        )
                );

            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }

        return responses;
    }

    public Video uploadToTwelveLabsAndSave(File file,String filename) {

        HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/tasks")
                .header("x-api-key", apiKey)
                .field("index_id", indexId)
                .field("video_file", file )
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
            if (video.getId() >= 20) { // ONLY 20TH VIDEO AND ONWARDS HAVE AWS STORED VIDEO, REMOVE THIS LOGIC AFTER
                String preSignedUrl = s3Service.generatePresignedGetUrl("tidier", "test/"+video.getName()).toString();
                videoResponseDTOList.add(new VideoResponseDTO(video.getName(),video.getVideoId(),preSignedUrl));
            }
            else{
                videoResponseDTOList.add(new VideoResponseDTO(video.getName(),video.getVideoId()));
            }
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



    public String getVideoUrl(String key) {
        String FAKE_KEY = "test/london-castle.mp4";
        return s3Service.generatePresignedGetUrl("tidier", FAKE_KEY ).toString();
    }
}
