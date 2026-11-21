package com.uniremington.valoor.chatbot.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeminiInteractionResponse(
        String id,
        List<GeminiStep> steps,
        GeminiUsage usage) {

    public String outputText() {
        if (steps == null) {
            return "";
        }
        return steps.stream()
                .filter(step -> "model_output".equals(step.type()))
                .filter(step -> step.content() != null)
                .flatMap(step -> step.content().stream())
                .filter(content -> "text".equals(content.type()))
                .map(GeminiContent::text)
                .filter(text -> text != null && !text.isBlank())
                .reduce("", String::concat);
    }

    public record GeminiStep(String type, List<GeminiContent> content) {
    }

    public record GeminiContent(String type, String text) {
    }

    public record GeminiUsage(
            @JsonProperty("total_input_tokens") Integer totalInputTokens,
            @JsonProperty("total_output_tokens") Integer totalOutputTokens,
            @JsonProperty("total_tokens") Integer totalTokens) {
    }
}
