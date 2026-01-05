package com.umerqureshicodes.tidier.videos;

import com.umerqureshicodes.tidier.montages.Montage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepo extends JpaRepository<Video, Long> {

    @Query(
            value = "SELECT * FROM videos v WHERE v.video_id IN (:videoIds)",
            nativeQuery = true
    )
    List<Video> findAllByVideoIds(@Param("videoIds") List<String> ids);

    List<Video> findAllByUserUsername(String username);

    Optional<Video> findByUserUsernameAndName(String email, String name);

}
