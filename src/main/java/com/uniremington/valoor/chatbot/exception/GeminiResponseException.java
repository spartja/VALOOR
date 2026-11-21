package com.uniremington.valoor.chatbot.exception;

public class GeminiResponseException extends RuntimeException {

    public GeminiResponseException() {
        super("Gemini devolvió una respuesta vacía.");
    }
}
