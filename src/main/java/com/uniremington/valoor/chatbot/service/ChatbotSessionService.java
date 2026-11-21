package com.uniremington.valoor.chatbot.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.uniremington.valoor.chatbot.dto.ChatHistoryMessage;
import com.uniremington.valoor.chatbot.dto.ChatRequest;
import com.uniremington.valoor.chatbot.dto.ChatResponse;
import com.uniremington.valoor.chatbot.dto.ChatSessionResponse;
import com.uniremington.valoor.chatbot.dto.ChatbotType;
import com.uniremington.valoor.chatbot.exception.ChatbotNotConfiguredException;
import com.uniremington.valoor.chatbot.exception.ChatSessionOwnerNotFoundException;
import com.uniremington.valoor.model.dao.UsuarioDAO;

@Service
public class ChatbotSessionService {

    private final ChatbotService chatbotService;
    private final ChatSessionPersistenceService persistenceService;
    private final UsuarioDAO usuarioDAO;

    public ChatbotSessionService(
            ChatbotService chatbotService,
            ChatSessionPersistenceService persistenceService,
            UsuarioDAO usuarioDAO) {
        this.chatbotService = chatbotService;
        this.persistenceService = persistenceService;
        this.usuarioDAO = usuarioDAO;
    }

    public ChatSessionResponse create(ChatbotType chatbotType, String email) {
        if (!chatbotService.isConfigured(chatbotType)) {
            throw new ChatbotNotConfiguredException();
        }
        Integer userId = findUserId(email);
        String sessionId = UUID.randomUUID().toString();
        persistenceService.create(sessionId, userId, chatbotType);
        return new ChatSessionResponse(sessionId);
    }

    public ChatResponse sendMessage(String sessionId, String message, String email) {
        Integer userId = findUserId(email);
        ChatSessionSnapshot session = persistenceService.findOwned(sessionId, userId);
        ChatbotTurn turn = chatbotService.sendTurn(
                new ChatRequest(session.chatbotType(), message),
                session.lastInteractionId());
        persistenceService.appendTurn(session, message, turn.response().reply(), turn.interactionId());
        return turn.response();
    }

    public List<ChatHistoryMessage> history(String sessionId, String email) {
        return persistenceService.history(sessionId, findUserId(email));
    }

    public void delete(String sessionId, String email) {
        persistenceService.delete(sessionId, findUserId(email));
    }

    private Integer findUserId(String email) {
        return usuarioDAO.findByEmailIgnoreCase(email)
                .orElseThrow(ChatSessionOwnerNotFoundException::new)
                .getIdUsuario();
    }
}
