package com.umerqureshicodes.tidier.montages;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.umerqureshicodes.tidier.users.AppUser;
import com.umerqureshicodes.tidier.videos.Video;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "montages")
@JsonIdentityInfo( // for one to many with videos, prevents infinite serialization
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id" // can use any unique identifier, usually PK is used
)
public class Montage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "montageSeqGen")
    @SequenceGenerator(name = "montageSeqGen", sequenceName = "montageSeq", allocationSize = 1)
    private Long id;
    private String name;
    @ManyToMany
    @JoinTable(
            name = "montage_videos", // junction table
            joinColumns = @JoinColumn(name = "montage_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private List<Video> videos = new ArrayList<>();
    private String prompt;
    private int duration;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    public Montage() {

    }
    public Montage(String name, String prompt, List<Video> videos, int duration, AppUser user) {
        this.name = name;
        this.prompt = prompt;
        this.videos.addAll(videos);
        this.duration = duration;
        this.user = user;
    }

    public Montage(String name, String prompt, AppUser user, int duration) {
        this.name = name;
        this.prompt = prompt;
        this.user = user;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        for (Video video : videos) {
            video.getMontages().add(this);
        }
        this.videos = videos;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}