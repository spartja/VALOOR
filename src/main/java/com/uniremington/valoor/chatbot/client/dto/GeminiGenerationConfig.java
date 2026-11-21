package com.uniremington.valoor.chatbot.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeminiGenerationConfig(
        Double temperature,
        @JsonProperty("max_output_tokens") Integer maxOutputTokens) {
}
