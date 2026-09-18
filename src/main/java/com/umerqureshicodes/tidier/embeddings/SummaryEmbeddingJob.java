package com.umerqureshicodes.tidier.embeddings;

import com.umerqureshicodes.tidier.TwelveLabs.TwelveLabsService;
import com.umerqureshicodes.tidier.montages.Montage;
import com.umerqureshicodes.tidier.montages.MontageRepo;
import com.umerqureshicodes.tidier.montages.MontageService;
import com.umerqureshicodes.tidier.videos.Video;
import com.umerqureshicodes.tidier.videos.VideoRepo;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

// Summaries can't be made at upload time because TwelveLabs is still indexing the video, and the call
// would fail. This job keeps trying in the background, which also covers videos uploaded before search existed.
@Component
public class SummaryEmbeddingJob {

    private static final int MAX_ATTEMPTS = 5;

    private final VideoRepo videoRepo;
    private final MontageRepo montageRepo;
    private final MontageService montageService;
    private final TwelveLabsService twelveLabsService;
    private final EmbeddingService embeddingService;

    public SummaryEmbeddingJob(VideoRepo videoRepo, MontageRepo montageRepo, MontageService montageService,
                              TwelveLabsService twelveLabsService, EmbeddingService embeddingService) {
        this.videoRepo = videoRepo;
        this.montageRepo = montageRepo;
        this.montageService = montageService;
        this.twelveLabsService = twelveLabsService;
        this.embeddingService = embeddingService;
    }

    // A few at a time so a backlog doesn't turn into a burst of API calls
    @Scheduled(initialDelayString = "${embeddings.job.initial-delay:30000}",
               fixedDelayString = "${embeddings.job.interval:60000}")
    @Transactional
    public void run() {
        summarizeVideos();
        embedMontages();
    }

    private void summarizeVideos() {
        List<Video> videos = videoRepo.findTop3ByEmbeddedFalseAndSummaryAttemptsLessThan(MAX_ATTEMPTS);
        for (Video video : videos) {
            String summary = video.getSummary();

            // A video that already has a summary only needs embedding, no TwelveLabs analysis
            if (summary == null) {
                // Counted before the call so a video that always fails eventually stops being retried
                video.setSummaryAttempts(video.getSummaryAttempts() + 1);

                // Still indexing, or no longer in the index at all
                if (video.getAssetId() == null) {
                    video.setAssetId(twelveLabsService.getAssetId(video.getVideoId()));
                    if (video.getAssetId() == null) {
                        System.out.println("No asset id yet for " + video.getName() + ", attempt " + video.getSummaryAttempts());
                        continue;
                    }
                }

                summary = twelveLabsService.summarizeVideo(video.getAssetId());
                if (summary == null) {
                    System.out.println("No summary yet for " + video.getName() + ", attempt " + video.getSummaryAttempts());
                    continue;
                }
                video.setSummary(summary);
            }

            if (embeddingService.embedAndStore(Embedding.Kind.VIDEO, video.getId(), video.getUser().getId(), summary)) {
                video.setEmbedded(true);
                System.out.println(video.getName() + " is now searchable");
            }
        }
    }

    private void embedMontages() {
        for (Montage montage : montageRepo.findTop3ByEmbeddedFalse()) {
            if (embeddingService.embedAndStore(Embedding.Kind.MONTAGE, montage.getId(),
                    montage.getUser().getId(), montageService.buildMontageText(montage))) {
                montage.setEmbedded(true);
                System.out.println("Embedded montage " + montage.getName());
            }
        }
    }
}
