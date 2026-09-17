package com.umerqureshicodes.tidier.WebSocket;

import com.umerqureshicodes.tidier.JWT.JwtAuthenticationToken;
import com.umerqureshicodes.tidier.JWT.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

// Authenticates the STOMP CONNECT frame with the access token, so messages can be sent to just that user
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final AuthenticationManager authenticationManager;

    public WebSocketAuthInterceptor(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new BadCredentialsException("Missing access token");
            }
            Authentication authResult = authenticationManager.authenticate(
                    new JwtAuthenticationToken(bearerToken.substring(7), JwtUtil.TokenType.ACCESS));
            // The user's name (their email) is what convertAndSendToUser targets
            accessor.setUser(authResult);
        }
        return message;
    }
}
