package com.umerqureshicodes.tidier.videos;

import com.umerqureshicodes.tidier.montages.MontageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class VideoServiceTest {

    @Mock
    VideoRepo videoRepo;

    @InjectMocks //this will create a real instance of VideoService and inject any fields with @Mock annotation
    VideoService videoService;

    @BeforeEach
    void setup() {
        // Inject a fake path into the @Value field
        ReflectionTestUtils.setField(videoService, "projectPath", "/Users/umerqureshi/Desktop/Computer Science/Personal Projects/SpringBoot/Tidier");
    }

}
