package com.umerqureshicodes.tidier.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    public static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String TYPE_CLAIM = "type";

    public enum TokenType { ACCESS, REFRESH }

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    //using Hmac which is symmetric, unlike RSA. Symmetric means same key is used for encrypt and decrypt (sign and verify)
    private final SecretKey key;

    // Secret comes from application-secrets.properties or the JWT_SECRET env var, must be at least 32 bytes
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, long expiryMinutes, TokenType type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .id(UUID.randomUUID().toString()) // lets a single refresh token be revoked on logout
                .claim(TYPE_CLAIM, type.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiryMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    // Returns null if the token is invalid, expired or not of the expected type
    public Claims parseClaims(String token, TokenType expectedType) {
        try{
            Claims claims = Jwts.parser()
                       .verifyWith(key)
                       .build()
                       .parseSignedClaims(token)
                       .getPayload();
            // Stops a 7-day refresh token being used as an access token and vice versa
            if (!expectedType.name().equals(claims.get(TYPE_CLAIM, String.class))) {
                return null;
            }
            return claims;
        }
        catch(Exception e){
            return null; // Invalid or expired Jwt
        }
    }

    public static String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {return  null;}
        for (Cookie cookie : cookies) {
            if(REFRESH_COOKIE_NAME.equals(cookie.getName())) { // The name that we put in the cookie, in Auth filter
                return cookie.getValue();
            }
        }
        return null;
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
