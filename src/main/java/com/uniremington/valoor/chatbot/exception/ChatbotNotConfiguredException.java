package com.uniremington.valoor.chatbot.exception;

public class ChatbotNotConfiguredException extends RuntimeException {

    public ChatbotNotConfiguredException() {
        super("El chatbot solicitado no está disponible.");
    }
}
