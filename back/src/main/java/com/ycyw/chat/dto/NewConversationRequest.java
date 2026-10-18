package com.ycyw.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/** Corps de {@code POST /api/conversations} : le client qui ouvre la conversation. */
public record NewConversationRequest(
    @Schema(description = "Identifiant du client qui ouvre la conversation", example = "1") @NotNull
        Long clientId) {}
