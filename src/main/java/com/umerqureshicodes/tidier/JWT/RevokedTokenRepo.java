package com.umerqureshicodes.tidier.JWT;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface RevokedTokenRepo extends JpaRepository<RevokedToken, String> {

    @Modifying
    @Query("delete from RevokedToken r where r.expiresAt < :now")
    void deleteExpired(@Param("now") Date now);
}
