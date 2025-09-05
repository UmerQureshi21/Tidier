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

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class VideoService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;
    @Value("${twelvelabs.index.id}")
    private String indexId;
    private final VideoRepo videoRepo;

    @Autowired
    public VideoService(VideoRepo videoRepo) {
        this.videoRepo = videoRepo;
    }

//    @Transactional
//    public void updateVideo(Video videoToUpdate, Prompt promptToAdd) {
//        videoToUpdate.getPrompts().add(promptToAdd); // just add it
//    }

    public List<VideoResponseDTO> uploadToDirectory(List<MultipartFile> files) {
        List<VideoResponseDTO> videoResponseDTOList = new ArrayList<>();
        try {
            // Save the files to the local file system
            for(MultipartFile file : files) {
                String filePath = "/Users/umerqureshi/Desktop/Computer Science/Personal Projects/SpringBoot/Tidier/uploads/" + file.getOriginalFilename();
                file.transferTo(new File(filePath));
                Video uploadedVid = uploadToTwelveLabsAndSave(filePath, file.getOriginalFilename());
                videoResponseDTOList.add(new VideoResponseDTO(uploadedVid.getName(),uploadedVid.getVideoId()));
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





}
