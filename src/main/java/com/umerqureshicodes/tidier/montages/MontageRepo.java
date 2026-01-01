package com.umerqureshicodes.tidier.montages;

import com.umerqureshicodes.tidier.videos.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MontageRepo extends JpaRepository<Montage, Long> {
    List<Montage> findAllByUserEmail(String email);

}
