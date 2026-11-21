package com.uniremington.valoor.chatbot.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.uniremington.valoor.chatbot.client.dto.GeminiInteractionRequest;
import com.uniremington.valoor.chatbot.client.dto.GeminiInteractionResponse;
import com.uniremington.valoor.chatbot.exception.GeminiConfigurationException;
import com.uniremington.valoor.chatbot.exception.GeminiUnavailableException;
import com.uniremington.valoor.config.GeminiProperties;

@Component
public class RestGeminiClient implements GeminiClient {

    private static final String API_REVISION = "2026-05-20";

    private final RestClient restClient;
    private final GeminiProperties properties;

    public RestGeminiClient(RestClient geminiRestClient, GeminiProperties properties) {
        this.restClient = geminiRestClient;
        this.properties = properties;
    }

    @Override
    public GeminiInteractionResponse create(GeminiInteractionRequest request) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new GeminiConfigurationException();
        }

        try {
            GeminiInteractionResponse response = restClient.post()
                    .uri("/interactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-goog-api-key", properties.getApiKey())
                    .header("Api-Revision", API_REVISION)
                    .body(request)
                    .retrieve()
                    .body(GeminiInteractionResponse.class);
            if (response == null) {
                throw new GeminiUnavailableException();
            }
            return response;
        } catch (ResourceAccessException exception) {
            throw new GeminiUnavailableException(exception);
        } catch (RestClientResponseException exception) {
            throw new GeminiUnavailableException();
        } catch (RestClientException exception) {
            throw new GeminiUnavailableException(exception);
        }
    }
}
