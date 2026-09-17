package com.umerqureshicodes.tidier.JWT;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

// A refresh token that was logged out, it stays here until the token would have expired anyway
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {
    @Id
    private String jti; // the token's unique id
    private Date expiresAt;

    protected RevokedToken() {}

    public RevokedToken(String jti, Date expiresAt) {
        this.jti = jti;
        this.expiresAt = expiresAt;
    }

    public String getJti() { return jti; }
    public Date getExpiresAt() { return expiresAt; }
}
