package com.umerqureshicodes.tidier.montages;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.umerqureshicodes.tidier.users.AppUser;
import com.umerqureshicodes.tidier.videos.Video;
import jakarta.persistence.*;

import java.util.ArrayList;
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
    @SequenceGenerator(name = "montageSeq", sequenceName = "montageSeq", allocationSize = 1)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    public Montage() {

    }
    public Montage(String name, String prompt, List<Video> videos) {
        this.name = name;
        this.prompt = prompt;
        this.videos.addAll(videos);
    }

    public Montage(String name, String prompt, AppUser user) {
        this.name = name;
        this.prompt = prompt;
        this.user = user;
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