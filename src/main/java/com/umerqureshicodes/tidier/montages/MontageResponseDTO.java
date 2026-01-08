package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.videos.VideoResponseDTO;

import java.util.Date;
import java.util.List;

public record MontageResponseDTO(
        String name,
        List<VideoResponseDTO> videos,
        String prompt,
        Date createdAt,
        int duration,
        String preSignedUrl
) {
}
