package com.uniremington.valoor.chatbot.dto;

import java.time.LocalDateTime;

public record ChatHistoryMessage(String role, String message, LocalDateTime createdAt) {
}
