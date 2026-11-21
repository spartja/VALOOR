package com.uniremington.valoor.chatbot.exception;

public class ChatSessionConflictException extends RuntimeException {

    public ChatSessionConflictException() {
        super("La sesión cambió durante el envío del mensaje.");
    }
}
