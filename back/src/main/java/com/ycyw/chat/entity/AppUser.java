package com.ycyw.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Utilisateur du tchat (table {@code app_user}). Les deux utilisateurs de démo sont créés par la
 * migration V2 : la PoC ne crée jamais d'utilisateur, d'où l'absence de constructeur public.
 */
@Entity
@Table(name = "app_user")
public class AppUser {

  @Id private Long id;

  @Column(name = "display_name")
  private String displayName;

  @Enumerated(EnumType.STRING)
  private Role role;

  protected AppUser() {
    // Requis par JPA.
  }

  public Long getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Role getRole() {
    return role;
  }
}
