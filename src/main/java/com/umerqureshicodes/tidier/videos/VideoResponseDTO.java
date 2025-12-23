package com.umerqureshicodes.tidier.videos;

public class VideoResponseDTO {
    private final String name;
    private final String videoId;
    private String previewUrl;

    public VideoResponseDTO(String name, String videoId, String previewUrl) {
        this.name = name;
        this.videoId = videoId;
        this.previewUrl = previewUrl;
    }

    public VideoResponseDTO(String name, String videoId) {
        this.name = name;
        this.videoId = videoId;
    }

    public String getName() {
        return name;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }


}
