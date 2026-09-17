package com.umerqureshicodes.tidier.JWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    public static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    //using Hmac which is symmetric, unlike RSA. Symmetric means same key is used for encrypt and decrypt (sign and verify)
    private final Key key;

    // Secret comes from application-secrets.properties or the JWT_SECRET env var, must be at least 32 bytes
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

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

    // Refresh token cookie, maxAgeSeconds = 0 tells the browser to delete it
    public String buildRefreshCookie(String value, long maxAgeSeconds, boolean isLocalhost) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .path("/refresh-token")
                .maxAge(maxAgeSeconds)
                .secure(!isLocalhost)
                .sameSite(isLocalhost ? "Lax" : "None")
                .build()
                .toString();
    }

    public static boolean isLocalhost(String serverName) {
        return serverName.equals("localhost") || serverName.equals("127.0.0.1");
    }
}
