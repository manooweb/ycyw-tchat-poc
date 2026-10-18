package com.ycyw.chat.controller;

import com.ycyw.chat.dto.MessageResponse;
import com.ycyw.chat.dto.SendMessageRequest;
import com.ycyw.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/** Messages en direct : reçus par WebSocket (STOMP), enregistrés, puis diffusés. */
@Controller
public class ChatMessageController {

  private final ChatService chatService;

  public ChatMessageController(ChatService chatService) {
    this.chatService = chatService;
  }

  /**
   * Le front envoie sur {@code /app/conversations/{id}/messages}. Le message est enregistré en
   * base, puis la valeur renvoyée est diffusée à tous les abonnés de {@code
   * /topic/conversations/{id}} : le client et l'agent le voient s'afficher en même temps.
   */
  @MessageMapping("/conversations/{id}/messages")
  @SendTo("/topic/conversations/{id}")
  public MessageResponse send(
      @DestinationVariable Long id, @Payload @Valid SendMessageRequest request) {
    return chatService.saveMessage(id, request.authorId(), request.content());
  }
}
