package com.ycyw.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/** Message écrit par un utilisateur dans une conversation (table {@code message}). */
@Entity
@Table(name = "message")
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "conversation_id")
  private Conversation conversation;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id")
  private AppUser author;

  private String content;

  @Column(name = "sent_at")
  private Instant sentAt;

  protected Message() {
    // Requis par JPA.
  }

  public Message(Conversation conversation, AppUser author, String content) {
    this.conversation = conversation;
    this.author = author;
    this.content = content;
    this.sentAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public Conversation getConversation() {
    return conversation;
  }

  public AppUser getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }

  public Instant getSentAt() {
    return sentAt;
  }
}
