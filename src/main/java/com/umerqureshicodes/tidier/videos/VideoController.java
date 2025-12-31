package com.umerqureshicodes.tidier.videos;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @GetMapping("/videos")
    @Cacheable(value = "videos", cacheManager = "cacheManager") // Looks under key named videos
    public List<VideoResponseDTO> getAllVideos() {
        return videoService.getVideos();
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @PostMapping("/videos")
    public List<VideoResponseDTO> uploadVideoToBucket(@RequestParam("files") List<MultipartFile> multipartFiles, @RequestParam("email") String email) {
        return videoService.save(multipartFiles, email);
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
}
