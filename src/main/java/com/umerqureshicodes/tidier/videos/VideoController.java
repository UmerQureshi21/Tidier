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

    /*
    * The "working directory" is wherever the Java process runs from. If you run locally from your IDE, it might be the project root. If you run packaged jar on a server, it might be different.

By using a relative path and creating the folder, your app will be portable and won’t break as long as the process can write there.
    * */

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/s3-test")
    public List<VideoResponseDTO> uploadVideoToBucket(@RequestParam("files") List<MultipartFile> multipartFiles) {
        return videoService.save(multipartFiles);
    }

    @GetMapping("/s3-test/{key}")
    public List<VideoResponseDTO> getVideoUrl(@PathVariable String key) {
        return videoService.getVideos();
    }



//    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
//    @PostMapping("/videos")
//    public List<VideoResponseDTO> uploadVideo(@RequestParam("files") List<MultipartFile> multipartFiles) {
//        return videoService.uploadToDirectory(multipartFiles);
//    }

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

    /*
    * Basically what im doing now is finding a way for frontend to call the montage
    * route everytime the montages folder gets updated, and you can use websockets
    * so im learning that rn 08/22 UPDATE: NO NEED FOR WEB SOCKET
    * */


}
