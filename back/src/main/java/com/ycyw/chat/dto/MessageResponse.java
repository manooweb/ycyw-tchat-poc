package com.ycyw.chat.dto;

import com.ycyw.chat.entity.Message;
import com.ycyw.chat.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/** Message tel qu'envoyé au front : même format pour l'historique (REST) et le direct (STOMP). */
public record MessageResponse(
    @Schema(description = "Identifiant, croissant dans l'ordre d'envoi", example = "12") Long id,
    @Schema(description = "Conversation du message", example = "3") Long conversationId,
    @Schema(description = "Identifiant de l'auteur", example = "1") Long authorId,
    @Schema(description = "Nom de l'auteur", example = "Camille Martin") String authorName,
    @Schema(description = "Rôle de l'auteur", example = "CLIENT") Role authorRole,
    @Schema(description = "Contenu, de 1 à 2 000 caractères", example = "Bonjour") String content,
    @Schema(description = "Date d'envoi", example = "2026-10-18T13:46:10Z") Instant sentAt) {

  public static MessageResponse from(Message message) {
    return new MessageResponse(
        message.getId(),
        message.getConversation().getId(),
        message.getAuthor().getId(),
        message.getAuthor().getDisplayName(),
        message.getAuthor().getRole(),
        message.getContent(),
        message.getSentAt());
  }
}
