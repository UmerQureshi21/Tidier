package com.umerqureshicodes.tidier.WebSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthenticationManager authenticationManager;

    public WebSocketConfig(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        // Clients subscribe to /user/queue/... to only get messages sent to them with convertAndSendToUser
        config.setUserDestinationPrefix("/user");
        // carries greeting message back to client on destinations prefixed with /topic
        config.setApplicationDestinationPrefixes("/app");
        // designates the app prefix for messages bound for methods annotated with @MessageMapping
        // e.g /app/hello will be the endpoint handled by the MessageController.greeting() method
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebSocketAuthInterceptor(authenticationManager));
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket")
                .setAllowedOriginPatterns("*"); // allow CORS if needed
    }

}