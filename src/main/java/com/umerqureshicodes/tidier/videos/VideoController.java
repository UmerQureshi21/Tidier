package com.umerqureshicodes.tidier.videos;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @PostMapping("/videos")
    public List<VideoResponseDTO> uploadVideoToBucket(@RequestParam("files") List<MultipartFile> multipartFiles) {
        return videoService.save(multipartFiles);
    }

    @GetMapping("/videos/{key}")
    public List<VideoResponseDTO> getVideoUrl(@PathVariable String key) {
        return videoService.getVideos();
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @DeleteMapping("/videos/{id}")
    public String deleteVideo(@PathVariable long id) {
        return videoService.deleteVideo(id) ;
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @GetMapping("/videos")
    public List<VideoResponseDTO> getAllVideos() {
        return videoService.getVideos();
    }
}
