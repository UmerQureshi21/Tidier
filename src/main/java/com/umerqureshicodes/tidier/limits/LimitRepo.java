package com.umerqureshicodes.tidier.limits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface LimitRepo extends JpaRepository<Limit, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Limit l where l.id = 1")
    Optional<Limit> getSingletonForUpdate();
}
