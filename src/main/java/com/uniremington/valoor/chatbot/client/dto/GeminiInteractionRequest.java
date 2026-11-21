package com.uniremington.valoor.chatbot.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiInteractionRequest(
        String model,
        String input,
        @JsonProperty("system_instruction") String systemInstruction,
        @JsonProperty("generation_config") GeminiGenerationConfig generationConfig,
        @JsonProperty("previous_interaction_id") String previousInteractionId) {
}
