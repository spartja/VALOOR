
package com.uniremington.valoor.chatbot.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.uniremington.valoor.chatbot.client.GeminiClient;
import com.uniremington.valoor.chatbot.client.dto.GeminiGenerationConfig;
import com.uniremington.valoor.chatbot.client.dto.GeminiInteractionRequest;
import com.uniremington.valoor.chatbot.client.dto.GeminiInteractionResponse;
import com.uniremington.valoor.chatbot.dto.ChatRequest;
import com.uniremington.valoor.chatbot.dto.ChatResponse;
import com.uniremington.valoor.chatbot.dto.ChatbotType;
import com.uniremington.valoor.chatbot.dto.SuggestedProduct;
import com.uniremington.valoor.chatbot.exception.ChatbotNotConfiguredException;
import com.uniremington.valoor.chatbot.exception.GeminiResponseException;
import com.uniremington.valoor.chatbot.exception.PromptUnavailableException;
import com.uniremington.valoor.config.ChatbotProperties;
import com.uniremington.valoor.config.GeminiProperties;

@Service
public class ChatbotService {

    private final GeminiClient geminiClient;
    private final GeminiProperties geminiProperties;
    private final ChatbotProperties chatbotProperties;
    private final ResourceLoader resourceLoader;

    public ChatbotService(
            GeminiClient geminiClient,
            GeminiProperties geminiProperties,
            ChatbotProperties chatbotProperties,
            ResourceLoader resourceLoader) {
        this.geminiClient = geminiClient;
        this.geminiProperties = geminiProperties;
        this.chatbotProperties = chatbotProperties;
        this.resourceLoader = resourceLoader;
    }

    public ChatResponse send(ChatRequest request) {
        return sendTurn(request, null).response();
    }

    public boolean isConfigured(ChatbotType chatbotType) {
        ChatbotProperties.BotSettings botSettings = chatbotProperties.getBots().get(chatbotType);
        return botSettings != null
                && botSettings.getPromptResource() != null
                && !botSettings.getPromptResource().isBlank();
    }

    public ChatbotTurn sendTurn(ChatRequest request, String previousInteractionId) {
        ChatbotProperties.BotSettings botSettings = chatbotProperties.getBots().get(request.chatbotType());
        if (botSettings == null
                || botSettings.getPromptResource() == null
                || botSettings.getPromptResource().isBlank()) {
            throw new ChatbotNotConfiguredException();
        }

        GeminiInteractionRequest interactionRequest = new GeminiInteractionRequest(
                geminiProperties.getModel(),
                request.message(),
                loadPrompt(botSettings.getPromptResource()),
                new GeminiGenerationConfig(botSettings.getTemperature(), geminiProperties.getMaxOutputTokens()),
                previousInteractionId);
        GeminiInteractionResponse interaction = geminiClient.create(interactionRequest);
        String reply = interaction.outputText();
        if (reply.isBlank() || interaction.id() == null || interaction.id().isBlank()) {
            throw new GeminiResponseException();
        }
        return new ChatbotTurn(
                interaction.id(),
                new ChatResponse(reply, List.<SuggestedProduct>of()));
    }

    private String loadPrompt(String resourceLocation) {
        Resource resource = resourceLoader.getResource(resourceLocation);
        try (InputStream input = resource.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8).trim();
        } catch (IOException exception) {
            throw new PromptUnavailableException(exception);
        }
    }
}
