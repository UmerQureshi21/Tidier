package com.umerqureshicodes.tidier.embeddings;

import com.umerqureshicodes.tidier.TwelveLabs.TwelveLabsService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EmbeddingService {

    // The embedding endpoint caps input at 500 tokens, this keeps well under it
    private static final int MAX_TEXT_LENGTH = 1500;
    // Below this, a match is closer to noise than to an answer. Tuned by hand: real matches score
    // around 0.5-0.9, unrelated text lands near 0.15-0.25
    private static final double MIN_SCORE = 0.30;

    private final EmbeddingRepo embeddingRepo;
    private final TwelveLabsService twelveLabsService;

    public EmbeddingService(EmbeddingRepo embeddingRepo, TwelveLabsService twelveLabsService) {
        this.embeddingRepo = embeddingRepo;
        this.twelveLabsService = twelveLabsService;
    }

    // Returns false if the text couldn't be embedded, the caller decides whether to retry later
    @Transactional
    public boolean embedAndStore(Embedding.Kind kind, Long refId, Long ownerId, String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String trimmedText = text.length() > MAX_TEXT_LENGTH ? text.substring(0, MAX_TEXT_LENGTH) : text;
        double[] vector = twelveLabsService.embedText(trimmedText);
        if (vector == null || vector.length == 0) {
            return false;
        }
        // Replace whatever was stored before, so re-embedding can't leave duplicates
        embeddingRepo.deleteAllByKindAndRefId(kind, refId);
        embeddingRepo.save(new Embedding(ownerId, kind, refId, 0, trimmedText, vector));
        return true;
    }

    @Transactional
    public void deleteFor(Embedding.Kind kind, Long refId) {
        embeddingRepo.deleteAllByKindAndRefId(kind, refId);
    }

    // Ranks this user's videos or montages against the query, best first
    public List<SearchHit> search(Long ownerId, Embedding.Kind kind, String query, int limit) {
        double[] queryVector = twelveLabsService.embedText(query);
        if (queryVector == null) {
            return List.of();
        }

        List<SearchHit> hits = new ArrayList<>();
        for (Embedding embedding : embeddingRepo.findAllByOwnerIdAndKind(ownerId, kind)) {
            if (embedding.getVector() == null || embedding.getVector().length != queryVector.length) {
                continue; // embedded with a different model, ignore rather than compare nonsense
            }
            double score = cosineSimilarity(queryVector, embedding.getVector());
            if (score >= MIN_SCORE) {
                hits.add(new SearchHit(embedding.getKind(), embedding.getRefId(), embedding.getText(), score));
            }
        }

        hits.sort(Comparator.comparingDouble(SearchHit::score).reversed());
        return hits.size() > limit ? hits.subList(0, limit) : hits;
    }

    static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0, magnitudeA = 0, magnitudeB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            magnitudeA += a[i] * a[i];
            magnitudeB += b[i] * b[i];
        }
        if (magnitudeA == 0 || magnitudeB == 0) {
            return 0;
        }
        return dot / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
    }
}
