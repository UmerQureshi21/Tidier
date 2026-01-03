package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.users.AppUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public MontageResponseDTO createMontage(@RequestBody MontageRequestDTO montageRequestDTO, @AuthenticationPrincipal AppUser appUser) {
        return montageService.createMontage(montageRequestDTO, appUser.getEmail());
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @DeleteMapping("/montages/{id}")
    public String deleteMontage(@PathVariable Long id) {
        return montageService.deleteMontage(id);
    }

    @CrossOrigin(origins = "#{@environment.getProperty('frontend.host')}")
    @GetMapping("/montages")
    @Cacheable(value = "montages", cacheManager = "cacheManager") // Looks under key named montages
    public List<MontageResponseDTO> getUrl(@AuthenticationPrincipal AppUser user){
        return montageService.getMontages(user.getEmail());
    }
}
