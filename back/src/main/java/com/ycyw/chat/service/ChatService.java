package com.ycyw.chat.service;

import com.ycyw.chat.dto.ConversationResponse;
import com.ycyw.chat.dto.MessageResponse;
import com.ycyw.chat.dto.UserResponse;
import com.ycyw.chat.entity.AppUser;
import com.ycyw.chat.entity.Conversation;
import com.ycyw.chat.entity.Role;
import com.ycyw.chat.repository.AppUserRepository;
import com.ycyw.chat.repository.ConversationRepository;
import com.ycyw.chat.repository.MessageRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Toute la logique du tchat : lire les utilisateurs, ouvrir une conversation, lire l'historique.
 * Les contrôleurs n'accèdent jamais directement aux repositories.
 */
@Service
public class ChatService {

  private final AppUserRepository userRepository;
  private final ConversationRepository conversationRepository;
  private final MessageRepository messageRepository;

  public ChatService(
      AppUserRepository userRepository,
      ConversationRepository conversationRepository,
      MessageRepository messageRepository) {
    this.userRepository = userRepository;
    this.conversationRepository = conversationRepository;
    this.messageRepository = messageRepository;
  }

  public List<UserResponse> listUsers() {
    return userRepository.findAll().stream().map(UserResponse::from).toList();
  }

  /** Un client voit ses conversations ; un agent les voit toutes. */
  public List<ConversationResponse> listConversations(Long userId) {
    AppUser user = findUser(userId);
    List<Conversation> conversations =
        user.getRole() == Role.AGENT
            ? conversationRepository.findAllByOrderByIdDesc()
            : conversationRepository.findByClientIdOrderByIdDesc(userId);
    return conversations.stream().map(ConversationResponse::from).toList();
  }

  /** Seul un client peut ouvrir une conversation. */
  @Transactional
  public ConversationResponse openConversation(Long clientId) {
    AppUser client = findUser(clientId);
    if (client.getRole() != Role.CLIENT) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Seul un client peut ouvrir une conversation");
    }
    return ConversationResponse.from(conversationRepository.save(new Conversation(client)));
  }

  public List<MessageResponse> history(Long conversationId) {
    return messageRepository.findByConversationIdOrderById(conversationId).stream()
        .map(MessageResponse::from)
        .toList();
  }

  private AppUser findUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
  }
}
