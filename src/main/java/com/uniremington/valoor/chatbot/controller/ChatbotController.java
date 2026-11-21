package com.uniremington.valoor.chatbot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniremington.valoor.chatbot.dto.ChatRequest;
import com.uniremington.valoor.chatbot.dto.ChatResponse;
import com.uniremington.valoor.chatbot.service.ChatbotService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ChatResponse send(@Valid @RequestBody ChatRequest request) {
        return chatbotService.send(request);
    }
}
