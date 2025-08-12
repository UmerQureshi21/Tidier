package com.umerqureshicodes.tidier.videos;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class VideoController {

    private final VideoService twelveLabsService;
    private final String frontendUrl = "http://localhost:5174";

    /*
    * The "working directory" is wherever the Java process runs from. If you run locally from your IDE, it might be the project root. If you run packaged jar on a server, it might be different.

By using a relative path and creating the folder, your app will be portable and won’t break as long as the process can write there.
    * */


    public VideoController(VideoService twelveLabsService) {
        this.twelveLabsService = twelveLabsService;
    }

//    @CrossOrigin(origins = frontendUrl)
//    @PostMapping("/upload")
//    public List<VideoResponseDTO> uploadFile(@RequestParam("files") List<MultipartFile> multipartFiles) {
//        return twelveLabsService.uploadVideoToFileSystem(multipartFiles);
//    }

    @CrossOrigin(origins = frontendUrl)
    @GetMapping("/videos")
    public List<VideoResponseDTO> getAllVideos() {
        return twelveLabsService.getVideos();
    }

    @GetMapping("/analyze")
    public String analyzeVideo(@RequestBody Video video) {
        return twelveLabsService.analyzeVideo(video);
    }
}
