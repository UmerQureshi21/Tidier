package com.umerqureshicodes.tidier.videos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.umerqureshicodes.tidier.montages.Montage;
import com.umerqureshicodes.tidier.users.AppUser;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="videos")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vidSeqGen")
    @SequenceGenerator(name = "vidSeqGen", sequenceName = "vidSeq", allocationSize = 1)
    private Long id;
    private String videoId; // TwelveLabs id of the indexed video
    private String assetId; // needed by the analyze endpoint, filled in once indexing finishes
    @ManyToMany(mappedBy = "videos")
    private List<Montage> montages = new ArrayList<>();
    private String name;
    @Column(columnDefinition = "text")
    private String summary; // TwelveLabs description of the video, this is what gets embedded
    // Default keeps the column addable to a table that already has rows
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int summaryAttempts; // stops a video that always fails from being retried forever
    // The embedding, not the summary, is what the search page needs
    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean embedded;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;


    public Video(String videoId, String name, List<Montage> montages, AppUser user) {
        this.videoId = videoId;
        this.name = name;
        this.montages.addAll(montages);
        this.user = user;
    }

    public Video(String videoId, String name, AppUser user) {
        this.videoId = videoId;
        this.name = name;
        this.user = user;
    }

    public List<Montage> getMontages() {
        return montages;
    }

    public void setMontages(List<Montage> montage) {
        for (Montage m : montage) {
            m.getVideos().add(this);
        }
        this.montages = montage;
    }

    public Video() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getName() {
        return name;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public int getSummaryAttempts() {
        return summaryAttempts;
    }

    public void setSummaryAttempts(int summaryAttempts) {
        this.summaryAttempts = summaryAttempts;
    }

    public AppUser getUser() {
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }


}