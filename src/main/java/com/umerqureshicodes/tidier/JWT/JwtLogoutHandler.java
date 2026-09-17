package com.umerqureshicodes.tidier.JWT;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

// Runs on POST /refresh-token/logout (under the cookie's path, so the browser sends the refresh token)
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final TokenRevocationService tokenRevocationService;

    public JwtLogoutHandler(JwtUtil jwtUtil, TokenRevocationService tokenRevocationService) {
        this.jwtUtil = jwtUtil;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Revoke the refresh token server side so it can't be used again even if it was copied
        String refreshToken = JwtUtil.extractRefreshToken(request);
        if (refreshToken != null) {
            Claims claims = jwtUtil.parseClaims(refreshToken, JwtUtil.TokenType.REFRESH);
            if (claims != null) {
                tokenRevocationService.revoke(claims);
            }
        }

        // Tell the browser to delete the cookie
        response.addHeader("Set-Cookie", jwtUtil.buildRefreshCookie(
                "", 0, JwtUtil.isLocalhost(request.getServerName())));
    }
}
