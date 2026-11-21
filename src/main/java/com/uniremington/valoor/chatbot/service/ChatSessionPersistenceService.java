package com.uniremington.valoor.chatbot.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.valoor.chatbot.dto.ChatHistoryMessage;
import com.uniremington.valoor.chatbot.dto.ChatMessageRole;
import com.uniremington.valoor.chatbot.dto.ChatbotType;
import com.uniremington.valoor.chatbot.entity.ChatMessage;
import com.uniremington.valoor.chatbot.entity.ChatSession;
import com.uniremington.valoor.chatbot.exception.ChatSessionConflictException;
import com.uniremington.valoor.chatbot.exception.ChatSessionNotFoundException;
import com.uniremington.valoor.chatbot.repository.ChatMessageRepository;
import com.uniremington.valoor.chatbot.repository.ChatSessionRepository;

@Service
public class ChatSessionPersistenceService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    public ChatSessionPersistenceService(
            ChatSessionRepository sessionRepository,
            ChatMessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public ChatSession create(String id, Integer userId, ChatbotType chatbotType) {
        ChatSession session = new ChatSession();
        session.setId(id);
        session.setUserId(userId);
        session.setChatbotType(chatbotType);
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public ChatSessionSnapshot findOwned(String sessionId, Integer userId) {
        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(ChatSessionNotFoundException::new);
        return toSnapshot(session);
    }

    @Transactional
    public void appendTurn(
            ChatSessionSnapshot expectedSession,
            String userMessage,
            String assistantMessage,
            String newInteractionId) {
        ChatSession session = sessionRepository
                .findOwnedForUpdate(expectedSession.id(), expectedSession.userId())
                .orElseThrow(ChatSessionNotFoundException::new);
        if (!Objects.equals(session.getLastInteractionId(), expectedSession.lastInteractionId())) {
            throw new ChatSessionConflictException();
        }

        ChatMessage userTurn = newMessage(session, ChatMessageRole.USER, userMessage);
        ChatMessage assistantTurn = newMessage(session, ChatMessageRole.ASSISTANT, assistantMessage);
        messageRepository.saveAll(List.of(userTurn, assistantTurn));
        session.setLastInteractionId(newInteractionId);
        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<ChatHistoryMessage> history(String sessionId, Integer userId) {
        sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(ChatSessionNotFoundException::new);
        return messageRepository.findBySession_IdOrderByCreatedAtAscIdAsc(sessionId)
                .stream()
                .map(message -> new ChatHistoryMessage(
                        message.getRole().name(),
                        message.getMessage(),
                        message.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void delete(String sessionId, Integer userId) {
        ChatSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(ChatSessionNotFoundException::new);
        sessionRepository.delete(session);
    }

    private ChatMessage newMessage(ChatSession session, ChatMessageRole role, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSession(session);
        chatMessage.setRole(role);
        chatMessage.setMessage(message);
        return chatMessage;
    }

    private ChatSessionSnapshot toSnapshot(ChatSession session) {
        return new ChatSessionSnapshot(
                session.getId(),
                session.getUserId(),
                session.getChatbotType(),
                session.getLastInteractionId());
    }
}
