package com.umerqureshicodes.tidier.embeddings;

// One ranked match, before it is turned into a video or montage response
public record SearchHit(Embedding.Kind kind, Long refId, String text, double score) {}
