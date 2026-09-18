package com.umerqureshicodes.tidier.embeddings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmbeddingRepo extends JpaRepository<Embedding, Long> {

    List<Embedding> findAllByOwnerIdAndKind(Long ownerId, Embedding.Kind kind);

    void deleteAllByKindAndRefId(Embedding.Kind kind, Long refId);
}
