package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.s3.S3Service;
import com.umerqureshicodes.tidier.videos.VideoRequestDTO;
import com.umerqureshicodes.tidier.videos.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;



import java.util.Arrays;
import java.util.List;


//@ExtendWith(MockitoExtension.class) only for mocking, but i need real s3 methods
@SpringBootTest
public class MontageServiceTest {

    @Autowired
    MontageService montageService; // real MontageService

    @Autowired
    S3Service s3Service; // real S3Service


//    @InjectMocks //this will create a real instance of MontageService and inject any fields with @Mock annotation
//    MontageService montageService;

    @BeforeEach
    void setup() {
        // Inject fake path for FFmpeg
        ReflectionTestUtils.setField(montageService, "projectPath",
                "/Users/umerqureshi/Desktop/personal-projects/SpringBoot/Tidier");
    }

    @Test
    void combineVideosShouldCombineAlreadyGivenVideos(){
        List<String> trimmedVideoNames = Arrays.asList("ny-streets.mp4-trimmed-0.mp4", "ny-streets.mp4-trimmed-1.mp4", "staten-island-streets.mp4-trimmed-2.mp4", "staten-island-streets.mp4-trimmed-3.mp4");
        //Temp dir should be var/folders/cq/blbr6twd5_x727qnpf8j4f5h0000gn/T/
        System.out.println(montageService.combineVideos(trimmedVideoNames ,"test-montage-of-roads"));

    }

    @Test
    void trimVideosShouldTrimVideos() {
        List<VideoRequestDTO> videoRequestDTOS = List.of(
                new VideoRequestDTO("ny-streets.mp4","69478582095d3c347d66d08b"),
                new VideoRequestDTO("staten-island-streets.mp4","69478589095d3c347d66d092")
        );
        List<String> timeStamps = List.of("00:00-00:06, 00:10-00:16","00:03-00:05, 00:8-00:13");
        MontageRequestDTO dto = new MontageRequestDTO(
                "test-montage",
                videoRequestDTOS,
                "roads",
                "Give all time intervals of roads, only tell me the intervals, nothing else, and in this format: 00:00-00:06, 01:02-01:09, ..."
        );

        System.out.println(montageService.trimVideos(timeStamps,videoRequestDTOS ));
        //Returns this: [ny-streets.mp4-trimmed-0.mp4, ny-streets.mp4-trimmed-1.mp4, staten-island-streets.mp4-trimmed-2.mp4, staten-island-streets.mp4-trimmed-3.mp4]


    }



}
