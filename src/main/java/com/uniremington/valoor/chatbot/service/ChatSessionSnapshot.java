package com.uniremington.valoor.chatbot.service;

import com.uniremington.valoor.chatbot.dto.ChatbotType;

public record ChatSessionSnapshot(
        String id,
        Integer userId,
        ChatbotType chatbotType,
        String lastInteractionId) {
}
