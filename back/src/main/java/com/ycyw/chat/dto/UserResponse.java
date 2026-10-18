package com.ycyw.chat.dto;

import com.ycyw.chat.entity.AppUser;
import com.ycyw.chat.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

/** Utilisateur tel qu'envoyé au front (le choix « Qui êtes-vous ? »). */
public record UserResponse(
    @Schema(description = "Identifiant", example = "1") Long id,
    @Schema(description = "Nom affiché", example = "Camille Martin") String displayName,
    @Schema(description = "Rôle", example = "CLIENT") Role role) {

  public static UserResponse from(AppUser user) {
    return new UserResponse(user.getId(), user.getDisplayName(), user.getRole());
  }
}
