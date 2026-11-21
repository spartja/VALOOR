package com.uniremington.valoor.chatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniremington.valoor.chatbot.dto.ChatResponse;
import com.uniremington.valoor.chatbot.dto.ChatbotType;
import com.uniremington.valoor.chatbot.exception.ChatSessionNotFoundException;
import com.uniremington.valoor.model.dao.UsuarioDAO;
import com.uniremington.valoor.model.entity.UsuarioEntity;

class ChatbotSessionServiceTest {

    private ChatbotService chatbotService;
    private ChatSessionPersistenceService persistenceService;
    private UsuarioDAO usuarioDAO;
    private ChatbotSessionService sessionService;

    @BeforeEach
    void setUp() {
        chatbotService = org.mockito.Mockito.mock(ChatbotService.class);
        persistenceService = org.mockito.Mockito.mock(ChatSessionPersistenceService.class);
        usuarioDAO = org.mockito.Mockito.mock(UsuarioDAO.class);
        sessionService = new ChatbotSessionService(chatbotService, persistenceService, usuarioDAO);

        UsuarioEntity user = new UsuarioEntity();
        user.setIdUsuario(42);
        when(usuarioDAO.findByEmailIgnoreCase("cliente@valoor.co")).thenReturn(Optional.of(user));
    }

    @Test
    void sendsOnlyOwnedSessionStateToGeminiAndPersistsTurn() {
        ChatSessionSnapshot session = new ChatSessionSnapshot(
                "session-id", 42, ChatbotType.ENTRENADOR, "previous-interaction");
        ChatResponse response = new ChatResponse("Respuesta", java.util.List.of());
        when(persistenceService.findOwned("session-id", 42)).thenReturn(session);
        when(chatbotService.sendTurn(any(), eq("previous-interaction")))
                .thenReturn(new ChatbotTurn("next-interaction", response));

        ChatResponse actual = sessionService.sendMessage(
                "session-id", "¿Qué más necesito?", "cliente@valoor.co");

        assertEquals(response, actual);
        verify(persistenceService).appendTurn(
                session, "¿Qué más necesito?", "Respuesta", "next-interaction");
    }

    @Test
    void doesNotCallGeminiForSessionNotOwnedByAuthenticatedUser() {
        when(persistenceService.findOwned("foreign-session", 42))
                .thenThrow(new ChatSessionNotFoundException());

        assertThrows(ChatSessionNotFoundException.class, () -> sessionService.sendMessage(
                "foreign-session", "Mi mensaje", "cliente@valoor.co"));

        verify(chatbotService, never()).sendTurn(any(), any());
        verify(persistenceService, never()).appendTurn(any(), any(), any(), any());
    }
}
