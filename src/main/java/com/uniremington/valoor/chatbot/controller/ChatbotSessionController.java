package com.uniremington.valoor.chatbot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniremington.valoor.chatbot.dto.ChatHistoryMessage;
import com.uniremington.valoor.chatbot.dto.ChatResponse;
import com.uniremington.valoor.chatbot.dto.ChatSessionResponse;
import com.uniremington.valoor.chatbot.dto.CreateChatSessionRequest;
import com.uniremington.valoor.chatbot.dto.SendChatMessageRequest;
import com.uniremington.valoor.chatbot.service.ChatbotSessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat/sessions")
public class ChatbotSessionController {

    private final ChatbotSessionService sessionService;

    public ChatbotSessionController(ChatbotSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<ChatSessionResponse> create(
            @Valid @RequestBody CreateChatSessionRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.create(request.chatbotType(), authentication.getName()));
    }

    @PostMapping("/{sessionId}/messages")
    public ChatResponse sendMessage(
            @PathVariable String sessionId,
            @Valid @RequestBody SendChatMessageRequest request,
            Authentication authentication) {
        return sessionService.sendMessage(sessionId, request.message(), authentication.getName());
    }

    @GetMapping("/{sessionId}/history")
    public List<ChatHistoryMessage> history(
            @PathVariable String sessionId,
            Authentication authentication) {
        return sessionService.history(sessionId, authentication.getName());
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> delete(
            @PathVariable String sessionId,
            Authentication authentication) {
        sessionService.delete(sessionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
