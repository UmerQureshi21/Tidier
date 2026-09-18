package com.umerqureshicodes.tidier.videos;

public class VideoResponseDTO {
    private final String name;
    private final String videoId;
    private String previewUrl;
    private boolean searchable; // true once the video has a summary and an embedding

    public VideoResponseDTO(String name, String videoId, String previewUrl, boolean searchable) {
        this.name = name;
        this.videoId = videoId;
        this.previewUrl = previewUrl;
        this.searchable = searchable;
    }

    public VideoResponseDTO(String name, String videoId, boolean searchable) {
        this.name = name;
        this.videoId = videoId;
        this.searchable = searchable;
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

    public boolean isSearchable() {
        return searchable;
    }


}
