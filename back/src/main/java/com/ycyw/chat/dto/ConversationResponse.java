package com.ycyw.chat.dto;

import com.ycyw.chat.entity.Conversation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/** Conversation telle qu'envoyée au front : on y ajoute le nom du client pour l'affichage. */
public record ConversationResponse(
    @Schema(description = "Identifiant", example = "3") Long id,
    @Schema(description = "Identifiant du client", example = "1") Long clientId,
    @Schema(description = "Nom du client", example = "Camille Martin") String clientName,
    @Schema(description = "Date d'ouverture", example = "2026-10-18T13:45:00Z") Instant createdAt) {

  public static ConversationResponse from(Conversation conversation) {
    return new ConversationResponse(
        conversation.getId(),
        conversation.getClient().getId(),
        conversation.getClient().getDisplayName(),
        conversation.getCreatedAt());
  }
}
