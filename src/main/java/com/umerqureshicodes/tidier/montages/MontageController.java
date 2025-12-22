package com.umerqureshicodes.tidier.montages;

import org.springframework.web.bind.annotation.*;

@RestController
public class MontageController {

    private final MontageService montageService;
    public MontageController(MontageService montageService) {
        this.montageService = montageService;
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @PostMapping("/montages")
    public MontageResponseDTO createMontage(@RequestBody MontageRequestDTO montageRequestDTO) {
        return montageService.createMontage(montageRequestDTO);
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @DeleteMapping("/montages/{id}")
    public String deleteMontage(@PathVariable Long id) {
        return montageService.deleteMontage(id);
    }

    @GetMapping("montages")
    public String getUrl(){
        return montageService.getVideoUrl("this doesn't matter");
    }
}
