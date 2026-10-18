package com.ycyw.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration de la WebSocket et du protocole STOMP.
 *
 * <ul>
 *   <li>{@code /ws} : adresse à laquelle le front ouvre la WebSocket ;
 *   <li>{@code /app/...} : messages envoyés par le front, traités par un {@code @MessageMapping} ;
 *   <li>{@code /topic/...} : messages diffusés à tous les abonnés.
 * </ul>
 *
 * <p>Le broker (le composant qui distribue les messages aux abonnés) est en mémoire : il suffit
 * pour une seule instance du back. Avec plusieurs instances, on le remplacerait par un relais vers
 * un broker externe comme RabbitMQ, sans changer les contrôleurs.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Le front Angular de développement tourne sur le port 4200.
    registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:4200");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/topic");
  }
}
