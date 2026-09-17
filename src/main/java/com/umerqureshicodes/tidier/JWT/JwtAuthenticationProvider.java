package com.umerqureshicodes.tidier.JWT;


import com.umerqureshicodes.tidier.users.AppUser;
import io.jsonwebtoken.Claims;
import com.umerqureshicodes.tidier.users.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

// Since I used custom token, have to provide custom provider
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenRevocationService tokenRevocationService;

    public JwtAuthenticationProvider(JwtUtil jwtUtil, UserService userService, TokenRevocationService tokenRevocationService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {

        // Extract raw token
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;

        Claims claims = jwtUtil.parseClaims(jwtToken.getToken(), jwtToken.getExpectedType());
        if (claims == null) {
            throw new BadCredentialsException("Invalid token");
        }
        // Refresh tokens that were logged out are rejected
        if (jwtToken.getExpectedType() == JwtUtil.TokenType.REFRESH && tokenRevocationService.isRevoked(claims.getId())) {
            throw new BadCredentialsException("Revoked token");
        }
        String username = claims.getSubject();
        System.out.println("Valid Token :)");

        AppUser user = userService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
