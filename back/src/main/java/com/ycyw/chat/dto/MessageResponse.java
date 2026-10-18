package com.ycyw.chat.dto;

import com.ycyw.chat.entity.Message;
import com.ycyw.chat.entity.Role;
import java.time.Instant;

/** Message tel qu'envoyé au front : même format pour l'historique (REST) et le direct (STOMP). */
public record MessageResponse(
    Long id,
    Long conversationId,
    Long authorId,
    String authorName,
    Role authorRole,
    String content,
    Instant sentAt) {

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
