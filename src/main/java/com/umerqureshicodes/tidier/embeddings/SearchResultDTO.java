package com.umerqureshicodes.tidier.embeddings;

// One search result, shaped for the search page
public record SearchResultDTO(
        String kind,      // "VIDEO" or "MONTAGE"
        Long id,
        String name,
        String url,       // presigned url for playback
        String summary,   // the text that matched, shown so the user sees why
        String prompt,    // montages only
        double score
) {}
