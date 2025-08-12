package com.umerqureshicodes.tidier.videos;

import com.umerqureshicodes.tidier.prompts.Prompt;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vidSeqGen")
    @SequenceGenerator(name = "vidSeqGen", sequenceName = "vidSeq", allocationSize = 1)
    private Long id;
    private String videoId;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "video_prompt", // new join table name
            joinColumns = @JoinColumn(name = "video_id"),// Foreign key for Video
            inverseJoinColumns = @JoinColumn(name = "prompt_id") // Foreign key for Prompt
    )
    private List<Prompt> prompts = new ArrayList<>();
    private boolean inMontage;
    private String name;

    public Video(String videoId, List<Prompt> prompts, boolean inMontage, String name) {
        this.videoId = videoId;
        this.prompts = prompts;
        this.inMontage = inMontage;
        this.name = name;
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

    public List<Prompt> getPrompts() {
        return prompts;
    }

    public void setPrompts(List<Prompt> prompts) {
        for (Prompt prompt : prompts) {
            if(!prompt.getVideos().contains(this)) {
                prompt.getVideos().add(this);
            }
        }
        this.prompts = prompts;
    }

    public boolean isInMontage() {
        return inMontage;
    }

    public void setInMontage(boolean inMontage) {
        this.inMontage = inMontage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}