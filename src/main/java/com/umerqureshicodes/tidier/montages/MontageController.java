package com.umerqureshicodes.tidier.montages;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @GetMapping("montages")
    @Cacheable(value = "montages", cacheManager = "cacheManager") // Looks under key named montages
    public List<MontageResponseDTO> getUrl(@RequestBody String userEmail){
        return montageService.getMontages(userEmail);
    }
}
