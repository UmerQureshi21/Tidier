package com.umerqureshicodes.tidier.timestamps;

import com.umerqureshicodes.tidier.prompts.Prompt;
import com.umerqureshicodes.tidier.videos.Video;
import jakarta.persistence.*;

@Entity
@Table(name = "timestamps")
public class Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "timestampSeqGen")
    @SequenceGenerator(name = "timestampSeqGen", sequenceName = "timestampSeq", allocationSize = 1)
    private Long id;
    private String startTime; //e.g "00:04"
    private String endTime;
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
    @ManyToOne
    @JoinColumn(name = "prompt_id")
    private Prompt prompt;

    public Timestamp(String startTime, String end, Video video, Prompt prompt) {
        this.startTime = startTime;
        this.endTime = end;
        this.video = video;
        this.prompt = prompt;
    }

    public Timestamp() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStart() {
        return startTime;
    }

    public void setStart(String startTime) {
        this.startTime = startTime;
    }

    public String getEnd() {
        return endTime;
    }

    public void setEnd(String endTime) {
        this.endTime = endTime;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Prompt getPrompt() {
        return prompt;
    }

    public void setPrompt(Prompt prompt) {
        this.prompt = prompt;
    }
}
