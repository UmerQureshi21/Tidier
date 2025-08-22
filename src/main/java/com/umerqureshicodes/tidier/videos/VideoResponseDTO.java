package com.umerqureshicodes.tidier.videos;

public class VideoResponseDTO {
    private final String name;
    private final String videoId;
    private boolean inMontage;

    public VideoResponseDTO( String name, String videoId, boolean inMontage ) {
        this.name = name;
        this.videoId = videoId;
        this.inMontage = inMontage;
    }

    public String getName() {
        return name;
    }

    public String getVideoId() {
        return videoId;
    }

    public boolean isInMontage() {
        return inMontage;
    }

    public void setInMontage(boolean inMontage) {
        this.inMontage = inMontage;
    }
}
