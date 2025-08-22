package com.umerqureshicodes.tidier.Utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TwelveLabsResponse {
    @JsonProperty("_id")
    private String id;

    @JsonProperty("video_id")
    private String videoId;

    public TwelveLabsResponse() {} // Jackson needs a no-arg constructor

    public String getId() {
        return id;
    }

    public String getVideoId() {
        return videoId;
    }
}
