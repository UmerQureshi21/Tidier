package com.umerqureshicodes.tidier.TwelveLabs;

import com.umerqureshicodes.tidier.Entities.Video;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class TwelveLabsController {

    private final TwelveLabsService twelveLabsService;

    /*
    * The "working directory" is wherever the Java process runs from. If you run locally from your IDE, it might be the project root. If you run packaged jar on a server, it might be different.

By using a relative path and creating the folder, your app will be portable and won’t break as long as the process can write there.
    * */


    public TwelveLabsController(TwelveLabsService twelveLabsService) {
        this.twelveLabsService = twelveLabsService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        return twelveLabsService.uploadVideoToFileSystem(multipartFile);
    }

    @GetMapping("/analyze")
    public String analyzeVideo(@RequestBody Video video) {
        return twelveLabsService.analyzeVideo(video);
    }
}
