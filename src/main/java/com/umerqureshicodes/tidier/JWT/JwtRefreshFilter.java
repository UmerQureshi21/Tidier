package com.umerqureshicodes.tidier.JWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
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
        String refreshToken = extractJwtFromRequest(request);
        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        //Provider that validates token, it wraps the refresh token in JwtAuthToken class
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(refreshToken);
        // Pass to manager
        Authentication authResult = authenticationManager.authenticate(authenticationToken);
        if(authResult.isAuthenticated()) {

            // If valid, we generate a new access token
            String newToken = jwtUtil.generateToken(authResult.getName(),15);
            response.setHeader("Authorization", "Bearer " + newToken);
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {return  null;}
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if("refreshToken".equals(cookie.getName())) { // The name that we put in the cookie, in Auth filter
                refreshToken = cookie.getValue();
            }
        }
        return refreshToken;
    }
}
