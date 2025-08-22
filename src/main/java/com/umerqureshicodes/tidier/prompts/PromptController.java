package com.umerqureshicodes.tidier.prompts;

import com.umerqureshicodes.tidier.Utilities.PostManString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class PromptController {

    private final PromptService promptService;

    public PromptController(PromptService promptService, @Value("${frontend.host}") String frontendUrl) {
        this.promptService = promptService;
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @PostMapping("/prompts")
    public String postPrompt(@RequestBody PromptRequestDTO promptRequestDTO) {
        return promptService.savePrompt(promptRequestDTO);
        // THIS ALSO ANALYZES EACH VID
    }

    @PostMapping("/ffmpeg")
    public String testTrimFfmpeg(@RequestBody PostManString request) {
        return promptService.trimVideos(request.getRequest());
    }

    //@GetMapping("/prompt/")
}
