package com.umerqureshicodes.tidier.s3;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;

@Service
public class S3Service {

    //1. download objects and upload object
    private final S3Client s3;

    public S3Service(S3Client s3Client) {
        this.s3 = s3Client;
    }

    public void putObject(String bucketName, String key, Path filePath) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.putObject(request, RequestBody.fromFile(filePath));
    }
    //Usage: putObject("tidier-videos", "uploads/video123.mp4", Paths.get(localPath));


    public Path getObjectByDownloadingToLocal(String bucketName, String key, Path destination) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.getObject(getObjectRequest, destination);
        return destination;
    }



}
