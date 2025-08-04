package com.umerqureshicodes.tidier.Entities;

public class Video {
    private String Id;
    private String prompt;
    public double temperature = 0.2; // the creativity of the response. The lower it is, the more consie and less creative but more accurate the response from the AI will be
    public boolean stream =  true;

    public Video(String id, String prompt) {
        Id = id;
        this.prompt = prompt;
    }

    public double getTemperature() {
        return temperature;
    }

    public boolean isStream() {
        return stream;
    }

    public Video() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
