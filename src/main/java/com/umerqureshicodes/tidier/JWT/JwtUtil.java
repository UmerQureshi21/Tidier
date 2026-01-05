package com.umerqureshicodes.tidier.JWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "your-secure-secret-key-min-32bytes";
    private static final Key key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    //using Hmac which is symmetric, unlike RSA. Symmetric means same key is used for encrypt and decrypt (sign and verify)

    public String generateToken(String username, long expiryMinutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiryMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public String validateAndExtractUsername(String token) {
        try{
            return Jwts.parser()
                       .setSigningKey(key)
                       .build()
                       .parseClaimsJws(token)
                       .getBody()
                       .getSubject();
            // Subject contains username
        }
        catch(Exception e){
            return null; // Invalid or expired Jwt
        }
    }
}
