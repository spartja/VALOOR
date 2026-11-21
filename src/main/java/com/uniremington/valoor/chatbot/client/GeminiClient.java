package com.uniremington.valoor.chatbot.client;

import com.uniremington.valoor.chatbot.client.dto.GeminiInteractionRequest;
import com.uniremington.valoor.chatbot.client.dto.GeminiInteractionResponse;

public interface GeminiClient {

    GeminiInteractionResponse create(GeminiInteractionRequest request);
}
