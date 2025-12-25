package com.umerqureshicodes.tidier.TwelveLabs;

import com.umerqureshicodes.tidier.Utilities.TwelveLabsTaskResponse;
import com.umerqureshicodes.tidier.Utilities.TwelveLabsTimeStampResponse;
import com.umerqureshicodes.tidier.videos.Video;
import com.umerqureshicodes.tidier.videos.VideoRequestDTO;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TwelveLabsService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;
    @Value("${twelvelabs.index.id}")
    private String indexId;

    public TwelveLabsTaskResponse indexVideo(File videoFile) {

        HttpResponse<TwelveLabsTaskResponse> response = Unirest.post("https://api.twelvelabs.io/v1.3/tasks")
                .header("x-api-key", apiKey)
                .field("index_id", indexId)
                .field("video_file", videoFile )
                .asObject(TwelveLabsTaskResponse.class);

        if(response.getStatus() == 200 || response.getStatus() == 201) {
            return response.getBody();
        }
        else{
            System.out.println("Error in 12Labs Service's indexVideo method: " + response.getStatus() + " " + response.getStatusText());
            return null;
        }
    }

    public TwelveLabsTimeStampResponse getIntervalsOfTopic(String videoId, String topic) {
        String requestBody = "{"
                + "\"video_id\": \"" + videoId + "\","
                + "\"prompt\": \"" + topic + "\","
                + "\"temperature\": 0.2,"
                + "\"stream\": false"
                + "}";

        HttpResponse<TwelveLabsTimeStampResponse> response =
                Unirest.post("https://api.twelvelabs.io/v1.3/analyze")
                        .header("x-api-key", apiKey)
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .asObject(TwelveLabsTimeStampResponse.class);

        if(response.getStatus() == 200 || response.getStatus() == 201) {
            return response.getBody();
        }
        else{
            System.out.println("Error in 12Labs Service's getIntervalsOfTopic method: " + response.getStatus() + " " + response.getStatusText());
            return null;
        }
    }
}
