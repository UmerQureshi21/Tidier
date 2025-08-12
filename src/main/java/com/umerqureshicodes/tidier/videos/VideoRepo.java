package com.umerqureshicodes.tidier.videos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepo extends JpaRepository<Video, Long> {
}
