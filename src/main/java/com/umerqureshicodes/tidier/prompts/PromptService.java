package com.umerqureshicodes.tidier.prompts;

import com.umerqureshicodes.tidier.videos.Video;
import com.umerqureshicodes.tidier.videos.VideoRepo;
import com.umerqureshicodes.tidier.videos.VideoRequestDTO;
import com.umerqureshicodes.tidier.videos.VideoService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class PromptService {

    private final PromptRepo promptRepo;
    private final VideoService videoService;
    @Value("${twelvelabs.api.key}")
    private String apiKey;

    public PromptService(PromptRepo promptRepo, VideoService videoService) {
        this.promptRepo = promptRepo;
        this.videoService = videoService;
    }



    /*
        MAKE SURE TO ADD SOMETHING THAT CREATES THE UPLOADS AND
        TRIMMED UPLOADS AND MONTAGES DDIRECOORY AND VIDEOS TEXTFILE BECAUSE I WILL BE IGNOREING
        IT IN GITIGNORE
     */





    public String savePrompt(PromptRequestDTO promptRequestDTO) {
        Prompt newPrompt = new Prompt();
        newPrompt.setSentence(promptRequestDTO.getSentence());
        newPrompt.setSelected(false);
        // make a Prompt object but add the Videos later because you have to fetch them from
        //DB and update their respective Prompts list to add this prompt
        //Then you set this prompts videos and save it

        List<Video> videos = new ArrayList<>();

        for (VideoRequestDTO v : promptRequestDTO.getVideoRequestDTOs()) {
            Video video = videoService.getVideoByVideoId(v.getVideoId());
            if (video == null) throw new RuntimeException("Video not found: " + v.getVideoId());

            videoService.updateVideo(video, newPrompt); // just add the new prompt
            videos.add(video);
        }

        newPrompt.setVideos(videos);
        promptRepo.save(newPrompt); // join table updated automatically

        return analyzeVideoWithPrompt(newPrompt);
    }

    //https://docs.twelvelabs.io/v1.3/api-reference/analyze-videos/analyze
    public String analyzeVideoWithPrompt(Prompt prompt) {


        StringBuilder timestamps = new StringBuilder(); // basically just a String but built to do += to, using append() instead

        for(Video video : prompt.getVideos()) {
            String requestBody = "{"
                    + "\"video_id\": \"" + video.getVideoId() + "\","
                    + "\"prompt\": \"" + prompt.getSentence() + "\","
                    + "\"temperature\": 0.2,"
                    + "\"stream\": false"
                    + "}";

            HttpResponse<String> response = Unirest.post("https://api.twelvelabs.io/v1.3/analyze")
                    .header("x-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .asString();

            if(response.getStatus() == 200 || response.getStatus() == 201) {
                timestamps.append(response.getBody()).append(", ");
            }
        }

    return timestamps.toString();

    }

    public String trimVideos(String timeStamps){
        List<HashMap<String,String>> intervals = new ArrayList<>();
        List<String> trimmedVideosToCombine = new ArrayList<>();

        //timestamps in format of 00:00-00:04, 00:04-00:08, 00:11-00:13
        for(String timeStamp : timeStamps.split(", ")) {
            String[] times = timeStamp.split("-");
            HashMap<String,String> interval = new HashMap<>();
            interval.put("start", "00:"+times[0]+".000");
            interval.put("end", "00:"+times[1]+".000");
            intervals.add(interval);
        }

        //USE shard-and-bridge-from-castle.mp4
        int i = 0;
        for(HashMap<String,String> interval : intervals)
        {
            try {
                String inputPath = "/Users/umerqureshi/Desktop/Computer Science/Personal Projects/SpringBoot/Tidier/uploads/shard-and-bridge-from-castle.mp4";
                String outputPath = "/Users/umerqureshi/Desktop/Computer Science/Personal Projects/SpringBoot/Tidier/trimmed-uploads/shard-and-bridge-from-castle-trimmed-"+i+".mp4";
                trimmedVideosToCombine.add("shard-and-bridge-from-castle-trimmed-"+i+".mp4");
                StringBuilder printInterval = new StringBuilder();
                printInterval.append("start ").append(interval.get("start")).append("\n").append("end ").append(interval.get("end")).append("\n");
                System.out.println(printInterval.toString());
//                ProcessBuilder pb = new ProcessBuilder(
//                        "ffmpeg",
//                        "-i", inputPath,
//                        "-ss", interval.get("start"),
//                        "-to", interval.get("end"),
//                        "-c:v", "copy",
//                        "-c:a", "copy",
//                        outputPath
//                );
//
//                What you lose with the new method:
//
//                Speed ⚡
//
//                -c copy = instant, just cuts without touching pixels/audio.
//
//                        libx264 + aac = full re-encode. Depending on clip length & machine, could take seconds → minutes.
//
//                        File size 💾
//
//                Re-encoding may produce larger files than the original, since it’s not a byte-for-byte copy.
//
//                You can control this with -crf (e.g., 23 is default, 18–20 = higher quality, bigger file).
//
//                    Slight quality loss 🎥
//
//                    Any time you re-encode lossy formats (like H.264 + AAC), you’re recompressing → small degradation.
//
//                    At -crf 23, most people won’t see it, but it’s not 100% identical like -c copy.
//
//                            What you gain:
//
//✅ Works in Finder previews, QuickTime, Safari, iPhone, iPad, Chrome, etc.
//
//✅ Proper audio-video sync (sometimes -c copy produces desync issues if you cut on non-keyframes).
//
//✅ Future-proofing: every device expects H.264 + AAC.
                ProcessBuilder pb = new ProcessBuilder(
                        "ffmpeg",
                        "-i", inputPath,
                        "-ss", interval.get("start"),
                        "-to", interval.get("end"),
                        "-c:v", "libx264",
                        "-crf", "23",
                        "-preset", "fast",
                        "-c:a", "aac",
                        "-b:a", "192k",
                        outputPath
                );

                pb.redirectErrorStream(true); // merge stderr into stdout
                Process process = pb.start();

                // Capture output (optional, for debugging)
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                int exitCode = process.waitFor();
                System.out.println("FFmpeg finished with exit code " + exitCode);
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            i++;
        }

        return combineVideos(trimmedVideosToCombine,"shard-and-bridge-from-castle-montage.mp4");
    }

    public  String combineVideos(List<String> trimmedFiles, String outputFileName) {
        try {
            // 1. Create videos.txt
            File listFile = new File("videos.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile))) {
                for (String file : trimmedFiles) {
                    writer.write("file 'trimmed-uploads/" + file + "'\n");
                }
            }

            // 2. Run ffmpeg with re-encoding for compatibility
            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-f", "concat", "-safe", "0",
                    "-i", "videos.txt",
                    "-c:v", "libx264", "-crf", "23", "-preset", "veryfast",
                    "-c:a", "aac",
                    outputFileName
            );
            // the problem is it gets the filename from trimmedvideos, but it trues to loko for that
            // file assuming that its in the root directory, so i prepended each line
            // in videos.txt

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Log output in case of errors
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                Path projectRoot = Path.of(System.getProperty("user.dir")); //returns the directory where the Java process was started (usually your project root when you run the app)
                Path source = projectRoot.resolve("shard-and-bridge-from-castle-montage.mp4");
                Path target = projectRoot.resolve("frontend/public/montages/shard-and-bridge-from-castle-montage.mp4");

                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);


                return "✅ Successfully combined videos into " + outputFileName;
            } else {
                return "❌ Failed to combine videos (exit code " + exitCode + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }
}
