package com.uniremington.valoor.chatbot.dto;

import java.math.BigDecimal;

public record SuggestedProduct(
        Integer id,
        String nombre,
        String motivo,
        BigDecimal precio) {
}
