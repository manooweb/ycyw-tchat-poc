package com.ycyw.chat.repository;

import com.ycyw.chat.entity.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Accès à la table {@code message}. */
public interface MessageRepository extends JpaRepository<Message, Long> {

  /** Historique d'une conversation, dans l'ordre d'envoi. */
  List<Message> findByConversationIdOrderById(Long conversationId);
}
