package com.uniremington.valoor.chatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.uniremington.valoor.chatbot.client.GeminiClient;
import com.uniremington.valoor.chatbot.client.dto.GeminiInteractionRequest;
import com.uniremington.valoor.chatbot.client.dto.GeminiInteractionResponse;
import com.uniremington.valoor.chatbot.dto.ChatRequest;
import com.uniremington.valoor.chatbot.dto.ChatResponse;
import com.uniremington.valoor.chatbot.dto.ChatbotType;
import com.uniremington.valoor.chatbot.exception.ChatbotNotConfiguredException;
import com.uniremington.valoor.chatbot.exception.GeminiResponseException;
import com.uniremington.valoor.chatbot.exception.PromptUnavailableException;
import com.uniremington.valoor.config.ChatbotProperties;
import com.uniremington.valoor.config.GeminiProperties;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private GeminiClient geminiClient;

    @Mock
    private GeminiProperties geminiProperties;

    @Mock
    private ChatbotProperties chatbotProperties;

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private ChatbotService chatbotService;

    @Test
    @DisplayName("CP-01: isConfigured debe retornar true cuando el bot tiene un promptResource válido")
    void isConfigured_CuandoEstaConfigurado_RetornaTrue() {
        ChatbotType tipoBot = mock(ChatbotType.class);

        ChatbotProperties.BotSettings settings = new ChatbotProperties.BotSettings();
        settings.setPromptResource("classpath:prompts/asistente.txt");

        when(chatbotProperties.getBots()).thenReturn(Map.of(tipoBot, settings));

        boolean resultado = chatbotService.isConfigured(tipoBot);

        assertTrue(resultado, "Debería retornar true porque el promptResource está configurado");
    }

    @Test
    @DisplayName("CP-02: isConfigured debe retornar false cuando el bot no está configurado")
    void isConfigured_CuandoNoEstaConfigurado_RetornaFalse() {
        ChatbotType tipoBot = mock(ChatbotType.class);

        when(chatbotProperties.getBots()).thenReturn(Map.of());

        boolean resultado = chatbotService.isConfigured(tipoBot);

        assertFalse(resultado, "Debería retornar false porque el bot no existe en el mapa");
    }

    @Test
    @DisplayName("CP-03: send debe procesar un mensaje válido y retornar la respuesta de Gemini")
    void send_ConDatosValidos_RetornaChatResponse() throws IOException {
        ChatbotType tipoBot = mock(ChatbotType.class);
        ChatRequest request = new ChatRequest(tipoBot, "Hola, necesito información");

        ChatbotProperties.BotSettings settings = new ChatbotProperties.BotSettings();
        settings.setPromptResource("classpath:prompts/asistente.txt");
        settings.setTemperature(0.7);

        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream())
                .thenReturn(new ByteArrayInputStream("Eres un asistente útil".getBytes(StandardCharsets.UTF_8)));

        when(chatbotProperties.getBots()).thenReturn(Map.of(tipoBot, settings));
        when(resourceLoader.getResource("classpath:prompts/asistente.txt")).thenReturn(mockResource);
        when(geminiProperties.getModel()).thenReturn("gemini-2.5-flash");
        when(geminiProperties.getMaxOutputTokens()).thenReturn(500);

        GeminiInteractionResponse respuestaGemini = mock(GeminiInteractionResponse.class);
        when(respuestaGemini.id()).thenReturn("int-001");
        when(respuestaGemini.outputText()).thenReturn("¡Hola! Claro, dime en qué puedo ayudarte.");
        when(geminiClient.create(any(GeminiInteractionRequest.class))).thenReturn(respuestaGemini);

        ChatResponse respuesta = chatbotService.send(request);

        assertNotNull(respuesta);
        assertEquals("¡Hola! Claro, dime en qué puedo ayudarte.", respuesta.reply());
        verify(geminiClient, times(1)).create(any(GeminiInteractionRequest.class));
    }

    @Test
    @DisplayName("CP-04: sendTurn debe lanzar ChatbotNotConfiguredException cuando el bot no existe")
    void sendTurn_CuandoBotNoEstaConfigurado_LanzaExcepcion() {
        ChatbotType tipoBot = mock(ChatbotType.class);
        ChatRequest request = new ChatRequest(tipoBot, "Hola");

        when(chatbotProperties.getBots()).thenReturn(Map.of());

        assertThrows(ChatbotNotConfiguredException.class, () -> {
            chatbotService.sendTurn(request, null);
        });

        verifyNoInteractions(geminiClient);
    }

    @Test
    @DisplayName("CP-05: sendTurn debe lanzar PromptUnavailableException cuando falla la lectura del prompt")
    void sendTurn_CuandoFallaLecturaDelPrompt_LanzaPromptUnavailableException() throws IOException {
        ChatbotType tipoBot = mock(ChatbotType.class);
        ChatRequest request = new ChatRequest(tipoBot, "Hola");

        ChatbotProperties.BotSettings settings = new ChatbotProperties.BotSettings();
        settings.setPromptResource("classpath:prompts/inexistente.txt");

        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream()).thenThrow(new IOException("Archivo no encontrado"));

        when(chatbotProperties.getBots()).thenReturn(Map.of(tipoBot, settings));
        when(resourceLoader.getResource("classpath:prompts/inexistente.txt")).thenReturn(mockResource);

        assertThrows(PromptUnavailableException.class, () -> {
            chatbotService.sendTurn(request, null);
        });

        verifyNoInteractions(geminiClient);
    }

    @Test
    @DisplayName("CP-06: sendTurn debe lanzar GeminiResponseException cuando Gemini devuelve texto vacío")
    void sendTurn_CuandoGeminiRespondeVacio_LanzaGeminiResponseException() throws IOException {
        ChatbotType tipoBot = mock(ChatbotType.class);
        ChatRequest request = new ChatRequest(tipoBot, "Hola");

        ChatbotProperties.BotSettings settings = new ChatbotProperties.BotSettings();
        settings.setPromptResource("classpath:prompts/asistente.txt");
        settings.setTemperature(0.7);

        Resource mockResource = mock(Resource.class);
        when(mockResource.getInputStream())
                .thenReturn(new ByteArrayInputStream("Eres un asistente útil".getBytes(StandardCharsets.UTF_8)));

        when(chatbotProperties.getBots()).thenReturn(Map.of(tipoBot, settings));
        when(resourceLoader.getResource("classpath:prompts/asistente.txt")).thenReturn(mockResource);
        when(geminiProperties.getModel()).thenReturn("gemini-2.5-flash");
        when(geminiProperties.getMaxOutputTokens()).thenReturn(500);

        GeminiInteractionResponse respuestaVacia = mock(GeminiInteractionResponse.class);
        when(respuestaVacia.outputText()).thenReturn("   ");
        when(geminiClient.create(any(GeminiInteractionRequest.class))).thenReturn(respuestaVacia);

        assertThrows(GeminiResponseException.class, () -> {
            chatbotService.sendTurn(request, null);
        });
    }
}
