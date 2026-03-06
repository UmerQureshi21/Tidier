package com.umerqureshicodes.tidier.TwelveLabs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TwelveLabsTaskResponse(
        @JsonProperty("_id") String id,
        @JsonProperty("video_id") String videoId,
        @JsonProperty("status") String status,
        @JsonProperty("index_id") String indexId,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt,
        @JsonProperty("estimated_time") String estimatedTime
) {}
