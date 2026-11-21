package com.uniremington.valoor.chatbot.dto;

import java.util.List;

public record ChatResponse(
        String reply,
        List<SuggestedProduct> suggestedProducts) {
}
