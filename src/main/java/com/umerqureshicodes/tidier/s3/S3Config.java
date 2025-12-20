package com.umerqureshicodes.tidier.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public S3Client s3Client() { // Later need to add a second implementation for testing, wont actually connect
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }
}
