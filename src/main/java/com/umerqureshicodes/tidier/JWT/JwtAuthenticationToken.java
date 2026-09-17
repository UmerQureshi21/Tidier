package com.umerqureshicodes.tidier.JWT;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;
    // Access tokens are only accepted as Bearer headers, refresh tokens only from the cookie
    private final JwtUtil.TokenType expectedType;

    public JwtAuthenticationToken(String token, JwtUtil.TokenType expectedType) {
        super(null);
        this.token = token;
        this.expectedType = expectedType;
        setAuthenticated(false);
    }

    public String getToken() {
        return token;
    }

    public JwtUtil.TokenType getExpectedType() {
        return expectedType;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
