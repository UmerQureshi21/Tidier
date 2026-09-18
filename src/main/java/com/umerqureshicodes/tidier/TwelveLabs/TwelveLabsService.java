package com.umerqureshicodes.tidier.TwelveLabs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class TwelveLabsService {

    @Value("${twelvelabs.api.key}")
    private String apiKey;
    @Value("${twelvelabs.index.id}")
    private String indexId;
    public static final String EMBEDDING_MODEL = "marengo3.0";
    // pegasus1.2 was sunset, and pegasus1.5 takes a video object instead of a video_id
    public static final String ANALYZE_MODEL = "pegasus1.5";

    public TwelveLabsTaskResponse indexVideo(File videoFile) throws JsonProcessingException {

        HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/tasks")
                .header("x-api-key", apiKey)
                .field("index_id", indexId)
                .field("video_file", videoFile)
                .asString();

        System.out.println("RESPONSE BODY: "+ response.getBody());

        if(response.getStatus() != 200 && response.getStatus() != 201) {
            System.out.println("Error in 12Labs Service's indexVideo method: " + response.getStatus() + " " + response.getBody());
            return null;
        }

        // Need to use ObjectMapper because the response class uses @JsonProperty
        ObjectMapper mapper = new ObjectMapper();
        TwelveLabsTaskResponse body =
                mapper.readValue(response.getBody(), TwelveLabsTaskResponse.class);
        System.out.println("Video properly uploaded to 12 labs index! "+ body);
        return body;
    }

    public TwelveLabsTimeStampResponse getIntervalsOfTopic(String assetId, String topic) {
        return analyze(assetId, topic);
    }

    // The asset id needed by analyze is only on the video record, not in the upload task response.
    // Returns null while the video is still indexing, or if it is no longer in the index
    public String getAssetId(String videoId) {
        HttpResponse<String> response =
                Unirest.get("https://api.twelvelabs.io/v1.3/indexes/" + indexId + "/videos/" + videoId)
                        .header("x-api-key", apiKey)
                        .asString();

        if (response.getStatus() != 200) {
            System.out.println("Error in 12Labs Service's getAssetId method: " + response.getStatus() + " " + response.getBody());
            return null;
        }
        try {
            JsonNode body = new ObjectMapper().readTree(response.getBody());
            JsonNode assetId = body.get("asset_id");
            return assetId == null || assetId.isNull() ? null : assetId.asText();
        } catch (JsonProcessingException e) {
            System.out.println("Error in 12Labs Service's getAssetId method: " + e.getMessage());
            return null;
        }
    }

    // The summary is what gets embedded for search. The embedding endpoint caps input at 500 tokens,
    // so the prompt asks for a short summary
    public String summarizeVideo(String assetId) {
        TwelveLabsTimeStampResponse response = analyze(assetId,
                "Summarize this video in about 120 words. Describe the scenery, setting, time of day, "
                        + "weather, mood and the activities people are doing. Use plain descriptive sentences.");
        if (response == null || response.data() == null || response.data().isBlank()) {
            return null;
        }
        return response.data().trim();
    }

    //https://docs.twelvelabs.io/v1.3/api-reference/analyze-videos/analyze
    private TwelveLabsTimeStampResponse analyze(String assetId, String prompt) {
        // Serialized with Jackson so quotes or backslashes in the prompt can't break the JSON
        String requestBody;
        try {
            requestBody = new ObjectMapper().writeValueAsString(Map.of(
                    "video", Map.of("type", "asset_id", "asset_id", assetId),
                    "model_name", ANALYZE_MODEL,
                    "prompt", prompt,
                    "temperature", 0.2,
                    "stream", false
            ));
        } catch (JsonProcessingException e) {
            System.out.println("Error in 12Labs Service's analyze method: " + e.getMessage());
            return null;
        }

        HttpResponse<TwelveLabsTimeStampResponse> response =
                Unirest.post("https://api.twelvelabs.io/v1.3/analyze")
                        .header("x-api-key", apiKey)
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .asObject(TwelveLabsTimeStampResponse.class);

        if(response.getStatus() == 200 || response.getStatus() == 201) {
            return response.getBody();
        }
        else{ // if response.getStatus() == 429 then ive done more than 50 requests
            System.out.println("Error in 12Labs Service's analyze method: " + response.getStatus() + " " + response.getStatusText());
            return null;
        }
    }

    //https://docs.twelvelabs.io/v1.3/api-reference/create-embeddings-v2/create-embeddings
    // Queries and stored text must use the same model or the vectors aren't comparable
    public double[] embedText(String text) {
        String requestBody;
        try {
            requestBody = new ObjectMapper().writeValueAsString(Map.of(
                    "input_type", "text",
                    "model_name", EMBEDDING_MODEL,
                    "text", Map.of("input_text", text)
            ));
        } catch (JsonProcessingException e) {
            System.out.println("Error in 12Labs Service's embedText method: " + e.getMessage());
            return null;
        }

        HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/embed-v2")
                .header("x-api-key", apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .asString();

        if (response.getStatus() != 200 && response.getStatus() != 201) {
            System.out.println("Error in 12Labs Service's embedText method: " + response.getStatus() + " " + response.getBody());
            return null;
        }

        try {
            TwelveLabsEmbeddingResponse body =
                    new ObjectMapper().readValue(response.getBody(), TwelveLabsEmbeddingResponse.class);
            if (body.data() == null || body.data().isEmpty() || body.data().getFirst().embedding() == null) {
                System.out.println("Error in 12Labs Service's embedText method: no embedding in response");
                return null;
            }
            List<Double> embedding = body.data().getFirst().embedding();
            double[] vector = new double[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                vector[i] = embedding.get(i);
            }
            return vector;
        } catch (JsonProcessingException e) {
            System.out.println("Error in 12Labs Service's embedText method: " + e.getMessage());
            return null;
        }
    }

    //https://docs.twelvelabs.io/v1.3/api-reference/videos/delete
    public boolean deleteVideo(String videoId) {
        HttpResponse<String> response =
                Unirest.delete("https://api.twelvelabs.io/v1.3/indexes/" + indexId + "/videos/" + videoId)
                        .header("x-api-key", apiKey)
                        .asString();

        if (response.getStatus() != 200 && response.getStatus() != 204) {
            System.out.println("Error in 12Labs Service's deleteVideo method: " + response.getStatus() + " " + response.getBody());
            return false;
        }
        return true;
    }

    //public bro dont have total vidoes time  have 600 minutes for indexing
}
