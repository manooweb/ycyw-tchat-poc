package com.ycyw.chat.dto;

import com.ycyw.chat.entity.Conversation;
import java.time.Instant;

/** Conversation telle qu'envoyée au front : on y ajoute le nom du client pour l'affichage. */
public record ConversationResponse(Long id, Long clientId, String clientName, Instant createdAt) {

  public static ConversationResponse from(Conversation conversation) {
    return new ConversationResponse(
        conversation.getId(),
        conversation.getClient().getId(),
        conversation.getClient().getDisplayName(),
        conversation.getCreatedAt());
  }
}
