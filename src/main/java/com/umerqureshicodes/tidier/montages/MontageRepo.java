package com.umerqureshicodes.tidier.montages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MontageRepo extends JpaRepository<Montage, Long> {
}
