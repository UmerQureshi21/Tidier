package com.umerqureshicodes.tidier.prompts;

import com.umerqureshicodes.tidier.videos.Video;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="prompts")
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promptSeqGen")
    @SequenceGenerator(name = "promptSeqGen", sequenceName = "promptSeq", allocationSize = 1)
    private Long id;
    private String sentence;
    @ManyToMany(mappedBy = "prompts")
    List<Video> videos = new ArrayList<>();
    private boolean isSelected;

    public Prompt(String sentence, List<Video> videos, boolean isSelected) {
        this.sentence = sentence;
        this.videos = videos;
        this.isSelected = isSelected;
    }


    public Prompt() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        for (Video video : videos) {
            if(!video.getPrompts().contains(this)) {
                video.getPrompts().add(this);
            }
        }
        this.videos = videos;

    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
