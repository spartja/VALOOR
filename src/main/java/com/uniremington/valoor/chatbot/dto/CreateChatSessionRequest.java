package com.uniremington.valoor.chatbot.dto;

import jakarta.validation.constraints.NotNull;

public record CreateChatSessionRequest(@NotNull ChatbotType chatbotType) {
}
