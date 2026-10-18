package com.ycyw.chat.repository;

import com.ycyw.chat.entity.Conversation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Accès à la table {@code conversation}. Spring Data déduit la requête du nom des méthodes. */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

  /** Conversations d'un client, les plus récentes d'abord. */
  List<Conversation> findByClientIdOrderByIdDesc(Long clientId);

  /** Toutes les conversations, les plus récentes d'abord (vue de l'agent). */
  List<Conversation> findAllByOrderByIdDesc();
}
