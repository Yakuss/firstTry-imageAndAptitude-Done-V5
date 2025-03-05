package com.example.firstTry.security;

import com.example.firstTry.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            String token = accessor.getFirstNativeHeader("Authorization");
//
//            // Add missing check for token presence/format
//            if (token == null || !token.startsWith("Bearer ")) {
//                throw new AuthenticationCredentialsNotFoundException("Missing or invalid token");
//            }
//
//            token = token.substring(7);
//            if (!jwtService.isTokenValid(token)) {
//                throw new AuthenticationCredentialsNotFoundException("Invalid token");
//            }
//        }
//        return message;
//    }
@Override
public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
        String token = accessor.getFirstNativeHeader("Authorization");
        System.out.println("WebSocket Token: " + token); // Debug token

        try {
            Claims claims = jwtService.extractAllClaims(token.substring(7));
            System.out.println("JWT Roles: " + claims.get("roles")); // Verify roles
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
        }
    }
    return message;
}

}
