package com.umerqureshicodes.tidier.embeddings;

import com.umerqureshicodes.tidier.montages.Montage;
import com.umerqureshicodes.tidier.montages.MontageRepo;
import com.umerqureshicodes.tidier.montages.MontageService;
import com.umerqureshicodes.tidier.s3.S3Service;
import com.umerqureshicodes.tidier.videos.Video;
import com.umerqureshicodes.tidier.videos.VideoRepo;
import com.umerqureshicodes.tidier.videos.VideoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SearchService {

    private static final int MAX_RESULTS = 10;

    private final EmbeddingService embeddingService;
    private final VideoRepo videoRepo;
    private final MontageRepo montageRepo;
    private final VideoService videoService;
    private final MontageService montageService;
    private final S3Service s3Service;

    public SearchService(EmbeddingService embeddingService, VideoRepo videoRepo, MontageRepo montageRepo,
                         VideoService videoService, MontageService montageService, S3Service s3Service) {
        this.embeddingService = embeddingService;
        this.videoRepo = videoRepo;
        this.montageRepo = montageRepo;
        this.videoService = videoService;
        this.montageService = montageService;
        this.s3Service = s3Service;
    }

    public List<SearchResultDTO> search(String query, Embedding.Kind kind, Long userId, String userEmail) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        List<SearchResultDTO> results = new ArrayList<>();
        for (SearchHit hit : embeddingService.search(userId, kind, query.trim(), MAX_RESULTS)) {
            // Looked up with the owner check, so a stale embedding can't expose someone else's video
            if (hit.kind() == Embedding.Kind.VIDEO) {
                Optional<Video> video = videoRepo.findByIdAndUserUsername(hit.refId(), userEmail);
                video.ifPresent(v -> results.add(new SearchResultDTO(
                        "VIDEO", v.getId(), v.getName(),
                        videoService.getVideoUrl(videoService.getS3Name(v)),
                        hit.text(), null, hit.score())));
            } else {
                Optional<Montage> montage = montageRepo.findByIdAndUserUsername(hit.refId(), userEmail);
                montage.ifPresent(m -> results.add(new SearchResultDTO(
                        "MONTAGE", m.getId(), m.getName(),
                        s3Service.generatePresignedGetUrl("tidier", montageService.getS3Key(m, userEmail)).toString(),
                        hit.text(), m.getPrompt(), hit.score())));
            }
        }
        return results;
    }
}
