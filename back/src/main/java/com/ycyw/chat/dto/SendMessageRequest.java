package com.ycyw.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Message envoyé par le front sur STOMP. Sans authentification, c'est le front qui indique
 * l'auteur : voir la section « Ce que la PoC ne fait pas » du README.
 */
public record SendMessageRequest(
    @NotNull Long authorId, @NotBlank @Size(max = 2000) String content) {}
