package com.uniremington.valoor.chatbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.uniremington.valoor.chatbot.dto.ChatErrorResponse;

@RestControllerAdvice
public class ChatbotExceptionHandler {

    @ExceptionHandler(ChatbotNotConfiguredException.class)
    public ResponseEntity<ChatErrorResponse> handleNotConfigured() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ChatErrorResponse("Este chatbot aún no está disponible."));
    }

    @ExceptionHandler({ ChatSessionNotFoundException.class, ChatSessionOwnerNotFoundException.class })
    public ResponseEntity<ChatErrorResponse> handleSessionNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ChatErrorResponse("No se encontró la sesión solicitada."));
    }

    @ExceptionHandler(ChatSessionConflictException.class)
    public ResponseEntity<ChatErrorResponse> handleSessionConflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ChatErrorResponse("La conversación cambió. Vuelve a enviar tu mensaje."));
    }

    @ExceptionHandler(GeminiConfigurationException.class)
    public ResponseEntity<ChatErrorResponse> handleConfiguration() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ChatErrorResponse("El servicio de chat no está configurado."));
    }

    @ExceptionHandler(GeminiUnavailableException.class)
    public ResponseEntity<ChatErrorResponse> handleGeminiUnavailable() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ChatErrorResponse("No pudimos generar una respuesta en este momento."));
    }

    @ExceptionHandler({ PromptUnavailableException.class, GeminiResponseException.class })
    public ResponseEntity<ChatErrorResponse> handleInternalFailure() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ChatErrorResponse("Ocurrió un problema al procesar tu mensaje."));
    }
}
