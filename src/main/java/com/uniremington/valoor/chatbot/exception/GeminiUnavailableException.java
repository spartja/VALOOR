package com.uniremington.valoor.chatbot.exception;

public class GeminiUnavailableException extends RuntimeException {

    public GeminiUnavailableException() {
        super("El servicio de chat no está disponible.");
    }

    public GeminiUnavailableException(Throwable cause) {
        super("El servicio de chat no está disponible.", cause);
    }
}
