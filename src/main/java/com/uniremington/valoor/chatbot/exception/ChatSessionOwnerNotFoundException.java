package com.uniremington.valoor.chatbot.exception;

public class ChatSessionOwnerNotFoundException extends RuntimeException {

    public ChatSessionOwnerNotFoundException() {
        super("El usuario autenticado no existe.");
    }
}
