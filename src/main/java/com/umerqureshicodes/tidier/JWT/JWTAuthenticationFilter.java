package com.umerqureshicodes.tidier.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umerqureshicodes.tidier.users.UserRequestDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//This filer responsible for loggins users in
//So we have created a filter, added it to filter chain
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
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

            // On success, generate JWT
            String token = jwtUtil.generateToken(authResult.getName(), 15);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write("{\"token\": \"" + token + "\"}");
            response.getWriter().flush();
            // Don't continue the chain for login
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid credentials\"}");
            response.getWriter().flush();
        }
    }
}
