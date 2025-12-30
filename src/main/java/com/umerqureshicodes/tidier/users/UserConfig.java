package com.umerqureshicodes.tidier.users;

import com.umerqureshicodes.tidier.JWT.JwtAuthenticationFilter;
import com.umerqureshicodes.tidier.JWT.JwtAuthenticationProvider;
import com.umerqureshicodes.tidier.JWT.JwtUtil;
import com.umerqureshicodes.tidier.JWT.JwtValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class UserConfig {

    private final JwtUtil jwtUtil;

    public UserConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Inject UserService and PasswordEncoder here instead of as fields
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtUtil jwtUtil, UserService userService) {
        return new JwtAuthenticationProvider(jwtUtil, userService);
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider daoAuthenticationProvider, JwtAuthenticationProvider jwtAuthenticationProvider) {
        return new ProviderManager(Arrays.asList(daoAuthenticationProvider,jwtAuthenticationProvider));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {

        // Authentication filter responsible for login
        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(authenticationManager, jwtUtil);

        // Validation filter responsible for checking Jwt in every request
        JwtValidationFilter jwtValidationFilter = new JwtValidationFilter(authenticationManager);

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(daoAuthenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/register", "/generate-token").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // generate token filter
                .addFilterAfter(jwtValidationFilter, JwtAuthenticationFilter.class); // validate token filter

        return http.build();
    }
}
