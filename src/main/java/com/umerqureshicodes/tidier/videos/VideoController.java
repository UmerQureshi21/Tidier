package com.umerqureshicodes.tidier.videos;

import com.umerqureshicodes.tidier.users.AppUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<VideoResponseDTO> getAllVideos(@AuthenticationPrincipal AppUser user) {
        // user is the one from JWT
        String email = user.getEmail();
        return videoService.getVideos(email);
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @PostMapping("/videos")
    public List<VideoResponseDTO> uploadVideoToBucket(
            @AuthenticationPrincipal AppUser user,
            @RequestParam("files") List<MultipartFile> multipartFiles
    ) {
        String email = user.getEmail();   // pulled from JWT-authenticated user
        return videoService.save(multipartFiles, email);
    }



    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @DeleteMapping("/videos/{id}")
    public String deleteVideo(@PathVariable long id) {
        return videoService.deleteVideo(id) ;
    }
}
