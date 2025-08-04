package com.umerqureshicodes.tidier.TwelveLabs;


import com.umerqureshicodes.tidier.Entities.Video;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class TwelveLabsService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;

    @Value("${twelvelabs.index.id}")
    private String indexId;


    public String uploadVideoToFileSystem(MultipartFile file) {
        String filePath = "/Users/umerqureshi/Desktop/Computer Science/Personal Projects/SpringBoot/Tidier/uploads/" + file.getOriginalFilename();

        try {
            // Save the file to the local file system
            file.transferTo(new File(filePath));

            // TODO: Save file metadata to the database via repository here

            return uploadVideoToTwelveLabs(filePath);
        } catch (IOException e) {
            e.printStackTrace(); // Or use a logger instead
            return "Failed to upload file: " + e.getMessage();
        }
    }

    public String uploadVideoToTwelveLabs(String path) {

        HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/tasks")
                .header("x-api-key", apiKey)
                .field("index_id", indexId)
                .field("video_file", new File(path) )
                .asString();

        return "Status: " + response.getStatus() + "\nResponse: " + response.getBody();
    }

//    public String analyzeVideo(Video video) {
//
//        if(video.getId() == null) {
//            return "Video id is null";
//        }
//
//        HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/videos/" + video.getId() + "/analyze")
//                .header("x-api-key", apiKey)
//                .header("Content-Type", "application/json")
//                .body("{\"prompt\": \"" + video.getPrompt() + "\"}")
//                .asString();
//
//        return "Status: " + response.getStatus() + "\nResponse: " + response.getBody();
//    }


    //https://docs.twelvelabs.io/v1.3/api-reference/analyze-videos/analyze
    public String analyzeVideo(Video video) {
        if (video.getId() == null) {
            return "Video ID is null";
        }

        String requestBody = "{"
                + "\"video_id\": \"" + video.getId() + "\","
                + "\"prompt\": \"" + video.getPrompt() + "\","
                + "\"temperature\": 0.2,"
                + "\"stream\": false"
                + "}";

        HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/analyze")
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .asString();

        return "Status: " + response.getStatus() + "\nResponse: " + response.getBody();
    }


}
