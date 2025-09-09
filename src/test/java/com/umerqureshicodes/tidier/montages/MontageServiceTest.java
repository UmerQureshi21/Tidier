package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.videos.VideoRequestDTO;
import com.umerqureshicodes.tidier.videos.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MontageServiceTest {

    @Mock
    MontageRepo montageRepo;
    @Mock
    VideoService videoService;

    @InjectMocks //this will create a real instance of MontageService and inject any fields with @Mock annotation
    MontageService montageService;

    @BeforeEach
    void setup() {
        // Inject a fake path into the @Value field
        ReflectionTestUtils.setField(montageService, "projectPath", "/Users/umerqureshi/Desktop/Computer Science/Personal Projects/SpringBoot/Tidier");
    }


    @Test
    void combineVideosShouldMakeVideosFileAndAddToMontages() {
        List<String> trimmedVideos = Arrays.asList(
                "ahmed-sis-wedding-dance.mov-trimmed-0.mp4",
                "ahmed-sis-wedding-dance.mov-trimmed-1.mp4",
                "ahmed-sis-wedding-dance.mov-trimmed-2.mp4",
                "on-london-bridge.mp4-trimmed-4.mp4",
                "on-london-bridge.mp4-trimmed-5.mp4",
                "TrailSense demo.f137.mp4-trimmed-3.mp4"
        );
        String outputFileName = "test-montage.mp4";
        System.out.println("Message: "+montageService.combineVideos(trimmedVideos,outputFileName));
    }

    @Test
    void trimVideosWithEmptyArray() {
        List<VideoRequestDTO> vids = Arrays.asList(
//                new VideoRequestDTO("ahmed-sis-wedding-dance.mov",""),
//                new VideoRequestDTO("TrailSense demo.f137.mp4",""),
                new VideoRequestDTO("on-london-bridge.mp4","")
                );
        List<String> timestamp = Arrays.asList(
                ""
//                "01:11-01:20",
//                "00:00-00:01, 00:04-00:05"
        );
        System.out.println("trimVideosWithEmptyArray");
        montageService.trimVideos(timestamp,vids);
    }
}
