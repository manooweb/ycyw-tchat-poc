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

/** Conversation ouverte par un client (table {@code conversation}). */
@Entity
@Table(name = "conversation")
public class Conversation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "client_id")
  private AppUser client;

  @Column(name = "created_at")
  private Instant createdAt;

  protected Conversation() {
    // Requis par JPA.
  }

  public Conversation(AppUser client) {
    this.client = client;
    this.createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public AppUser getClient() {
    return client;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
