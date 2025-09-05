package com.umerqureshicodes.tidier.Utilities;

public record TwelveLabsTimeStampResponse(
        String id,
        String data,
        String finish_reason,
        Usage usage
) {
    public record Usage(int output_tokens) {}
}