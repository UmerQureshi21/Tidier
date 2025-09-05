package com.umerqureshicodes.tidier.videos;

public class VideoResponseDTO {
    private final String name;
    private final String videoId;

    public VideoResponseDTO( String name, String videoId) {
        this.name = name;
        this.videoId = videoId;
    }

    public String getName() {
        return name;
    }

    public String getVideoId() {
        return videoId;
    }


}
