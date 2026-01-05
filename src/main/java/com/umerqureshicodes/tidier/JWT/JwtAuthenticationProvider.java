package com.umerqureshicodes.tidier.JWT;


import com.umerqureshicodes.tidier.users.AppUser;
import com.umerqureshicodes.tidier.users.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

// Since I used custom token, have to provide custom provider
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private JwtUtil jwtUtil;
    private UserService userService;

    public JwtAuthenticationProvider(JwtUtil jwtUtil, UserService userService ) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {

        // Extract raw token
        String token = ((JwtAuthenticationToken) authentication).getToken();

        String username = jwtUtil.validateAndExtractUsername(token);
        if (username == null) {
            throw new BadCredentialsException("Invalid token");
        }
        System.out.println("Valid Token :)");

        AppUser user = userService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
