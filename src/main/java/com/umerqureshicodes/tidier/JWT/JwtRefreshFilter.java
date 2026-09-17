package com.umerqureshicodes.tidier.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtRefreshFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtRefreshFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response , FilterChain filterChain) throws ServletException, IOException {

        if(!request.getServletPath().equals("/refresh-token")) {
            filterChain.doFilter(request,response);
            return;
        }

        // Extracts the 7-day-long token from the Cookie,
        String refreshToken = JwtUtil.extractRefreshToken(request);
        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        //Provider that validates token, it wraps the refresh token in JwtAuthToken class
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(refreshToken, JwtUtil.TokenType.REFRESH);
        // Pass to manager
        Authentication authResult;
        try {
            authResult = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            // Invalid, expired or revoked (logged out) refresh token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if(authResult.isAuthenticated()) {

            // If valid, we generate a new access token
            String newToken = jwtUtil.generateToken(authResult.getName(), 15, JwtUtil.TokenType.ACCESS);
            response.setHeader("Authorization", "Bearer " + newToken);
        }
    }
}
