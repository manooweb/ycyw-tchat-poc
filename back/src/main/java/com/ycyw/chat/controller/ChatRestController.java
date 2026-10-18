package com.ycyw.chat.controller;

import com.ycyw.chat.dto.ConversationResponse;
import com.ycyw.chat.dto.MessageResponse;
import com.ycyw.chat.dto.NewConversationRequest;
import com.ycyw.chat.dto.UserResponse;
import com.ycyw.chat.service.ChatService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** API REST : tout ce qui se lit au chargement de l'écran, et l'ouverture d'une conversation. */
@RestController
@RequestMapping("/api")
public class ChatRestController {

  private final ChatService chatService;

  public ChatRestController(ChatService chatService) {
    this.chatService = chatService;
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

  /** Ouvre une conversation pour un client. */
  @PostMapping("/conversations")
  @ResponseStatus(HttpStatus.CREATED)
  public ConversationResponse open(@Valid @RequestBody NewConversationRequest request) {
    return chatService.openConversation(request.clientId());
  }

  /** Historique complet d'une conversation, chargé quand on l'ouvre. */
  @GetMapping("/conversations/{id}/messages")
  public List<MessageResponse> history(@PathVariable Long id) {
    return chatService.history(id);
  }
}
