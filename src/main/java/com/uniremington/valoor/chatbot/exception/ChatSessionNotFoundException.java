package com.uniremington.valoor.chatbot.exception;

public class ChatSessionNotFoundException extends RuntimeException {

    public ChatSessionNotFoundException() {
        super("La sesión de chat no existe.");
    }
}
