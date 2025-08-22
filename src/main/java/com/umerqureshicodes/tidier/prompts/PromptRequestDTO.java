package com.umerqureshicodes.tidier.prompts;

import com.umerqureshicodes.tidier.videos.Video;
import com.umerqureshicodes.tidier.videos.VideoRequestDTO;

import java.util.List;

public class PromptRequestDTO {
    private String sentence;
    private List<VideoRequestDTO> videoRequestDTOs;

    public PromptRequestDTO(String sentence, List<VideoRequestDTO> videoRequestDTOs) {
        this.sentence = sentence;
        this.videoRequestDTOs = videoRequestDTOs;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public List<VideoRequestDTO> getVideoRequestDTOs() {
        return videoRequestDTOs;
    }

    public void setVideosRequestDTOs(List<VideoRequestDTO> videoRequestDTOs) {
        this.videoRequestDTOs = videoRequestDTOs;
    }
}
