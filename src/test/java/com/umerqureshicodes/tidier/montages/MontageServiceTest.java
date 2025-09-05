package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.videos.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void combineVideosShouldMakeVideosFileAndAddToMontages() {
        List<String> trimmedVideos = Arrays.asList(
                "shard-and-bridge-from-castle.mp4.mov-trimmed-0.mp4"
                ,"shard-and-bridge-from-castle.mp4.mov-trimmed-1.mp4",
                "shard-and-bridge-from-castle.mp4.mov-trimmed-2.mp4"
        );
        String outputFileName = "test-montage.mp4";
        System.out.println("Message: "+montageService.combineVideos(trimmedVideos,outputFileName));
    }
}
