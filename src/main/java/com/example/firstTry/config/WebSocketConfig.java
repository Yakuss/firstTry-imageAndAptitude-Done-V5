package com.example.firstTry.config;

import com.example.firstTry.security.AuthChannelInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //private final AuthChannelInterceptor authChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Prefix for notifications
        config.setApplicationDestinationPrefixes("/app"); // Prefix for sending messages
        config.setPreservePublishOrder(Boolean.TRUE); //👈 Critical for message ordering
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
                //.setAllowedOriginPatterns(String.valueOf(new DefaultHandshakeHandler()))
                //.withSockJS(); // Enables SockJS fallback
        System.out.println("[DEBUG] WebSocket endpoint registered at /ws");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
       // registration.interceptors(authChannelInterceptor);
        registration.taskExecutor().corePoolSize(10);
    }

//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
//        messageConverters.add(new StringMessageConverter());
//        messageConverters.add(new ByteArrayMessageConverter());
//        messageConverters.add(messageConverter());
//        return false;
//    }
//
//    @Bean
//    public MappingJackson2MessageConverter messageConverter() {
//        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//        converter.setObjectMapper(new ObjectMapper().registerModule(new JavaTimeModule()));
//        converter.setSerializedPayloadClass(String.class);
//        converter.setStrictContentTypeMatch(false);
//        return converter;
//    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(objectMapper);
        converter.setSerializedPayloadClass(String.class);
        return converter;
    }
}
