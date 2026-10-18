package com.ycyw.chat.controller;

import com.ycyw.chat.dto.ConversationResponse;
import com.ycyw.chat.dto.MessageResponse;
import com.ycyw.chat.dto.NewConversationRequest;
import com.ycyw.chat.dto.UserResponse;
import com.ycyw.chat.service.ChatService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST : tout ce qui se lit au chargement de l'écran, et l'ouverture d'une conversation. Les
 * messages, eux, s'envoient par WebSocket (voir ChatMessageController).
 */
@RestController
@RequestMapping("/api")
public class ChatRestController {

  private final ChatService chatService;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatRestController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
    this.chatService = chatService;
    this.messagingTemplate = messagingTemplate;
  }

  /** Les utilisateurs de démo, pour le choix « Qui êtes-vous ? ». */
  @GetMapping("/users")
  public List<UserResponse> users() {
    return chatService.listUsers();
  }

  @GetMapping("/conversations")
  public List<ConversationResponse> conversations(@RequestParam Long userId) {
    return chatService.listConversations(userId);
  }

  /** Ouvre une conversation et prévient en direct les agents abonnés à /topic/conversations. */
  @PostMapping("/conversations")
  @ResponseStatus(HttpStatus.CREATED)
  public ConversationResponse open(@Valid @RequestBody NewConversationRequest request) {
    ConversationResponse conversation = chatService.openConversation(request.clientId());
    messagingTemplate.convertAndSend("/topic/conversations", conversation);
    return conversation;
  }

  /** Historique complet d'une conversation, chargé quand on l'ouvre. */
  @GetMapping("/conversations/{id}/messages")
  public List<MessageResponse> history(@PathVariable Long id) {
    return chatService.history(id);
  }
}
