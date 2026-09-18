package com.umerqureshicodes.tidier.embeddings;

import jakarta.persistence.*;

// One embedded piece of text belonging to a video or a montage.
// Summaries are short enough to be a single chunk today, chunkIndex leaves room for longer text later
@Entity
@Table(name = "embeddings", indexes = {
        @Index(name = "idx_embeddings_owner_kind", columnList = "ownerId,kind"),
        @Index(name = "idx_embeddings_kind_ref", columnList = "kind,refId")
})
public class Embedding {

    public enum Kind { VIDEO, MONTAGE }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "embeddingSeqGen")
    @SequenceGenerator(name = "embeddingSeqGen", sequenceName = "embeddingSeq", allocationSize = 1)
    private Long id;

    private Long ownerId; // the AppUser the video or montage belongs to

    @Enumerated(EnumType.STRING)
    private Kind kind;

    private Long refId; // Video.id or Montage.id

    private int chunkIndex;

    @Column(columnDefinition = "text")
    private String text;

    // Stored as a Postgres double precision[], small enough to rank in Java at this scale
    @Column(columnDefinition = "double precision[]")
    private double[] vector;

    private int dim;

    protected Embedding() {}

    public Embedding(Long ownerId, Kind kind, Long refId, int chunkIndex, String text, double[] vector) {
        this.ownerId = ownerId;
        this.kind = kind;
        this.refId = refId;
        this.chunkIndex = chunkIndex;
        this.text = text;
        this.vector = vector;
        this.dim = vector.length;
    }

    public Long getId() { return id; }
    public Long getOwnerId() { return ownerId; }
    public Kind getKind() { return kind; }
    public Long getRefId() { return refId; }
    public int getChunkIndex() { return chunkIndex; }
    public String getText() { return text; }
    public double[] getVector() { return vector; }
    public int getDim() { return dim; }
}
