package com.ycyw.chat.dto;

import jakarta.validation.constraints.NotNull;

/** Corps de {@code POST /api/conversations} : le client qui ouvre la conversation. */
public record NewConversationRequest(@NotNull Long clientId) {}
