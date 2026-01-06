package com.umerqureshicodes.tidier.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umerqureshicodes.tidier.users.UserRequestDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//This filer responsible for loggins users in
//So we have created a filter, added it to filter chain
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Only handle POST /generate-token
        if (!request.getServletPath().equals("/generate-token") ||
                !"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Parse JSON body into DTO
            UserRequestDTO userRequestDTO =
                    objectMapper.readValue(request.getReader(), UserRequestDTO.class);

            // Build auth token (username + password), this one uses DaoAuth provider
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userRequestDTO.username(),
                            userRequestDTO.password()
                    );

            // Perform authentication
            Authentication authResult = authenticationManager.authenticate(authenticationToken);
            System.out.println("Authentication successful: " + authResult.isAuthenticated());


            if(authResult.isAuthenticated()) {
            String token = jwtUtil.generateToken(authResult.getName(), 15);
                System.out.println("Access token generated: " + token);
                response.setHeader("Authorization", "Bearer " + token) ;

            String refreshToken = jwtUtil.generateToken(authResult.getName(), 7*24*60);
            // Set refresh token in HttpOnly Cookie
            // We can also send it in response body but then client has to store in memory or in local storage
            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true); // prevent javascript from accessing
                boolean isLocalhost = request.getServerName().equals("localhost") ||
                        request.getServerName().equals("127.0.0.1");
                refreshCookie.setSecure(!isLocalhost);
                refreshCookie.setPath("/refresh-token"); // Cookie available only for refresh endpoint
                refreshCookie .setMaxAge(7*24*60*60);
                response.addCookie(refreshCookie);
            }

        } catch (Exception ex) {
            System.out.println("Exception in JwtAuthenticationFilter: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid credentials\"}");
            response.getWriter().flush();
        }
    }
}
