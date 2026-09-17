package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.users.AppUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class MontageController {

    private final MontageService montageService;
    public MontageController(MontageService montageService) {
        this.montageService = montageService;
    }

    @PostMapping("/montages")
    public MontageResponseDTO createMontage(@RequestBody MontageRequestDTO montageRequestDTO, @AuthenticationPrincipal AppUser appUser) {
        return montageService.createMontage(montageRequestDTO, appUser.getUsername());
    }

    @ExceptionHandler(MontageCreationException.class)
    public ResponseEntity<Map<String, String>> handleMontageCreationException(MontageCreationException e) {
        return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
    }

    @DeleteMapping("/montages/{id}")
    public String deleteMontage(@PathVariable Long id, @AuthenticationPrincipal AppUser appUser) {
        return montageService.deleteMontage(id, appUser.getUsername());
    }

    @GetMapping("/montages")
    @Cacheable(value = "montages", cacheManager = "cacheManager") // Looks under key named montages
    public List<MontageResponseDTO> getUrl(@AuthenticationPrincipal AppUser user){
        return montageService.getMontages(user.getUsername());
    }

    @GetMapping("/montages/most-vids")
    public MontageResponseDTO getMostVideoMontage(@AuthenticationPrincipal AppUser user){
        return montageService.getMostVideoMontage(user.getUsername());
    }

    @GetMapping("/montages/longest")
    public MontageResponseDTO getLongestMontage(@AuthenticationPrincipal AppUser user) throws IOException, InterruptedException {
        return montageService.getLongestMontage(user.getUsername());
    }
}
