package com.umerqureshicodes.tidier.ai;

// One ranked match from the AI service, before it is turned into a video or montage response
public record AiSearchHit(Long refId, String text, double score) {}
