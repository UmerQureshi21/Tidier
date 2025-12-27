package com.umerqureshicodes.tidier.FFmpeg;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class FFmpegService {

    public int convertToMp4(String inputFile, String outputFile) throws IOException, InterruptedException {
        String[] command = {
                "ffmpeg",
                "-y", // overwrite if exists
                "-i", inputFile,
                "-c:v", "libx264",
                "-c:a", "aac",
                outputFile
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (var reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null) {
                // optionally log ffmpeg output
            }
        }

        return process.waitFor();
    }

    public int trimVideo(String inputTempFilePath, String outputTempFilePath, String startInterval, String endInterval) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y", // overwrite if I need too
                "-i", inputTempFilePath,
                "-ss", startInterval,
                "-to", endInterval,
                "-c:v", "libx264",
                "-crf", "23",
                "-preset", "fast",
                "-c:a", "aac",
                "-b:a", "192k",
                outputTempFilePath
        );

        pb.redirectErrorStream(true); // merge stderr into stdout
        Process process = pb.start();

        // Capture output (optional, for debugging)
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        return process.waitFor();
    }

    public int combineVideo(String concatFilePath, String outputTempFilePath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y", "-f", "concat", "-safe", "0",
                "-i", concatFilePath,
                "-c:v", "libx264", "-crf", "23", "-preset", "veryfast",
                "-c:a", "aac",
                outputTempFilePath
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();
        // Log output in case of errors
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        return process.waitFor();
    }
}

