package com.ycyw.chat.dto;

import com.ycyw.chat.entity.AppUser;
import com.ycyw.chat.entity.Role;

/** Utilisateur tel qu'envoyé au front (le choix « Qui êtes-vous ? »). */
public record UserResponse(Long id, String displayName, Role role) {

  public static UserResponse from(AppUser user) {
    return new UserResponse(user.getId(), user.getDisplayName(), user.getRole());
  }
}
