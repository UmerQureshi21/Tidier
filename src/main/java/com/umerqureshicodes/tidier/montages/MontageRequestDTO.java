package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.videos.VideoRequestDTO;

import java.util.List;

public record MontageRequestDTO(
        String name,
        List<VideoRequestDTO> videoRequestDTOs,
        String prompt,
        String sentence,
        String userEmail
) {
}
