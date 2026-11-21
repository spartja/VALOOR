package com.uniremington.valoor.chatbot.exception;

public class GeminiConfigurationException extends RuntimeException {

    public GeminiConfigurationException() {
        super("Gemini API no está configurada.");
    }
}
