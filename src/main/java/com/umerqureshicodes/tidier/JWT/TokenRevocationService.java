package com.umerqureshicodes.tidier.JWT;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenRevocationService {

    private final RevokedTokenRepo revokedTokenRepo;

    public TokenRevocationService(RevokedTokenRepo revokedTokenRepo) {
        this.revokedTokenRepo = revokedTokenRepo;
    }

    @Transactional
    public void revoke(Claims refreshClaims) {
        // Expired tokens are rejected by the signature check anyway, so there's no need to keep them
        revokedTokenRepo.deleteExpired(new Date());
        revokedTokenRepo.save(new RevokedToken(refreshClaims.getId(), refreshClaims.getExpiration()));
    }

    public boolean isRevoked(String jti) {
        return jti == null || revokedTokenRepo.existsById(jti);
    }
}
