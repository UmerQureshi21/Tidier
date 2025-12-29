package com.umerqureshicodes.tidier.videos;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.umerqureshicodes.tidier.montages.Montage;
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
    private String videoId;
    @ManyToMany(mappedBy = "videos")
    private List<Montage> montages = new ArrayList<>();
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private com.umerqureshicodes.tidier.user.User user;


    public Video(String videoId, String name, List<Montage> montages) {
        this.videoId = videoId;
        this.name = name;
        this.montages.addAll(montages);
    }

    public Video(String videoId, String name) {
        this.videoId = videoId;
        this.name = name;
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

    public void setName(String name) {
        this.name = name;
    }


}