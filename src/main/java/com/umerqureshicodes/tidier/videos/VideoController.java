package com.umerqureshicodes.tidier.videos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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


//    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
//    @PostMapping("/upload")
//    public List<VideoResponseDTO> uploadFile(@RequestParam("files") List<MultipartFile> multipartFiles) {
//        return twelveLabsService.uploadVideoToFileSystem(multipartFiles);
//    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @GetMapping("/videos")
    public List<VideoResponseDTO> getAllVideos() {
        return videoService.getVideos();
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @GetMapping("/montages")
    public List<String> getAllMontages() {
        return videoService.getAllMontages();
    }

    /*
    * Basically what im doing now is finding a way for frontend to call the montage
    * route everytime the montages folder gets updated, and you can use websockets
    * so im learning that rn 08/22
    * */


}
