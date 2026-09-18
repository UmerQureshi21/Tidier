package com.umerqureshicodes.tidier.TwelveLabs;

import java.util.List;

// Response of POST /v1.3/embed-v2, the vector lives at data[0].embedding
public record TwelveLabsEmbeddingResponse(
        List<Embedding> data
) {
    public record Embedding(List<Double> embedding) {}
}
