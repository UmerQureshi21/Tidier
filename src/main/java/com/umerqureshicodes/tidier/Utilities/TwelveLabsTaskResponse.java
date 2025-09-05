package com.umerqureshicodes.tidier.Utilities;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TwelveLabsTaskResponse(
        @JsonProperty("_id") String taskId,
        @JsonProperty("video_id") String videoId
) {}
