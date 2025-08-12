package com.umerqureshicodes.tidier.videos;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.umerqureshicodes.tidier.Utilities.TwelveLabsResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;
    @Value("${twelvelabs.index.id}")
    private String indexId;
    private VideoRepo videoRepo;

    @Autowired
    public VideoService(VideoRepo videoRepo) {
        this.videoRepo = videoRepo;
    }

//    public List<VideoResponseDTO> uploadVideoToFileSystem(List<MultipartFile> files) {
//        List<VideoResponseDTO> videoResponseDTOList = new ArrayList<>();
//        try {
//            // Save the files to the local file system
//            for(MultipartFile file : files) {
//                String filePath = "/Users/umerqureshi/Desktop/Computer Science/Personal Projects/SpringBoot/Tidier/uploads/" + file.getOriginalFilename();
//                file.transferTo(new File(filePath));
//                Video uploadedVid = uploadVideoToTwelveLabs(filePath, file.getOriginalFilename());
//                videoResponseDTOList.add(new VideoResponseDTO(uploadedVid.getName(),uploadedVid.getVideoId(),true));
//            }
//            return videoResponseDTOList;
//        } catch (IOException e) {
//            e.printStackTrace(); // Or use a logger instead
//            System.out.println ("Failed to upload file: " + e.getMessage());
//            return null;
//        }
//    }
//
//    public Video uploadVideoToTwelveLabs(String path,String filename) {
//
//        HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/tasks")
//                .header("x-api-key", apiKey)
//                .field("index_id", indexId)
//                .field("video_file", new File(path) )
//                .asString();
//
//        if(response.getStatus() == 200 || response.getStatus() == 201) {
//            try{
//                ObjectMapper mapper = new ObjectMapper();
//                TwelveLabsResponse responseContainer = mapper.readValue(response.getBody(), TwelveLabsResponse.class);
//                Video video = new Video(responseContainer.getVideoId(),new ArrayList<>(),false,filename);
//                videoRepo.save(video);
//                return video;
//            }catch(Exception e){
//                e.printStackTrace();
//                System.out.println("Failed to upload file: " + e.getMessage());
//                return null;
//            }
//
//        }
//        else{
//            System.out.println("API error: " + response.getStatus());
//            return null;
//        }
//
//    }

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
                + "\"video_id\": \"" + video.getVideoId() + "\","
                + "\"prompt\": \"" + "this is an example prompt. just return this: example prompt was used" + "\","
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


    public List<VideoResponseDTO> getVideos() {
        List<VideoResponseDTO> videoResponseDTOList = new ArrayList<>();
        for (Video video : videoRepo.findAll()) {
            videoResponseDTOList.add(new VideoResponseDTO(video.getName(),video.getVideoId(),false));
        }
        return videoResponseDTOList;
    }
}
