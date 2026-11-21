package com.uniremington.valoor.chatbot.service;

import com.uniremington.valoor.chatbot.dto.ChatResponse;

public record ChatbotTurn(String interactionId, ChatResponse response) {
}
