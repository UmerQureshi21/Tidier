package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.videos.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MontageRepo extends JpaRepository<Montage, Long> {
    List<Montage> findAllByUserUsername(String username);

    Optional<Montage> findByIdAndUserUsername(Long id, String username);

}
