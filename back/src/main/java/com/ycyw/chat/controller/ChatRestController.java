package com.ycyw.chat.controller;

import com.ycyw.chat.dto.ConversationResponse;
import com.ycyw.chat.dto.MessageResponse;
import com.ycyw.chat.dto.NewConversationRequest;
import com.ycyw.chat.dto.UserResponse;
import com.ycyw.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tchat", description = "Utilisateurs, conversations et historique")
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
  @Operation(summary = "Lister les utilisateurs de démonstration")
  @ApiResponse(
      responseCode = "200",
      description = "Les deux utilisateurs créés par la migration V2.")
  @GetMapping("/users")
  public List<UserResponse> users() {
    return chatService.listUsers();
  }

  @Operation(summary = "Lister les conversations d'un utilisateur (toutes pour un agent)")
  @ApiResponse(responseCode = "200", description = "Conversations, les plus récentes d'abord.")
  @ApiResponse(responseCode = "400", description = "Paramètre userId absent ou invalide.")
  @ApiResponse(responseCode = "404", description = "Utilisateur introuvable.")
  @GetMapping("/conversations")
  public List<ConversationResponse> conversations(
      @Parameter(description = "Identifiant de l'utilisateur", example = "1") @RequestParam
          Long userId) {
    return chatService.listConversations(userId);
  }

  /** Ouvre une conversation et prévient en direct les agents abonnés à /topic/conversations. */
  @Operation(summary = "Ouvrir une conversation (client uniquement)")
  @ApiResponse(responseCode = "201", description = "Conversation ouverte et annoncée aux agents.")
  @ApiResponse(
      responseCode = "400",
      description = "clientId absent, ou l'utilisateur n'est pas un client.")
  @ApiResponse(responseCode = "404", description = "Utilisateur introuvable.")
  @PostMapping("/conversations")
  @ResponseStatus(HttpStatus.CREATED)
  public ConversationResponse open(@Valid @RequestBody NewConversationRequest request) {
    ConversationResponse conversation = chatService.openConversation(request.clientId());
    messagingTemplate.convertAndSend("/topic/conversations", conversation);
    return conversation;
  }

  /** Historique complet d'une conversation, chargé quand on l'ouvre. */
  @Operation(summary = "Lire l'historique d'une conversation")
  @ApiResponse(responseCode = "200", description = "Messages, du plus ancien au plus récent.")
  @ApiResponse(responseCode = "404", description = "Conversation introuvable.")
  @GetMapping("/conversations/{id}/messages")
  public List<MessageResponse> history(
      @Parameter(description = "Identifiant de la conversation", example = "1") @PathVariable
          Long id) {
    return chatService.history(id);
  }
}
