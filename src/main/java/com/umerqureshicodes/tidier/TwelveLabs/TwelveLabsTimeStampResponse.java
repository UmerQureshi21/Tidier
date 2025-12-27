package com.umerqureshicodes.tidier.TwelveLabs;

public record TwelveLabsTimeStampResponse(
        String id,
        String data,
        String finish_reason,
        Usage usage
) {
    public record Usage(int output_tokens) {}
}