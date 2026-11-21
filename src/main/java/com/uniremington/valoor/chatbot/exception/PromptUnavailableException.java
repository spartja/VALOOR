package com.uniremington.valoor.chatbot.exception;

public class PromptUnavailableException extends RuntimeException {

    public PromptUnavailableException(Throwable cause) {
        super("No se pudo cargar la configuración del chatbot.", cause);
    }
}
