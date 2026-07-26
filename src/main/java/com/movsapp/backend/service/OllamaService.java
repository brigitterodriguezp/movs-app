package com.movsapp.backend.service;

import com.movsapp.backend.dto.OllamaGenerateRequest;
import com.movsapp.backend.dto.OllamaGenerateResponse;
import com.movsapp.backend.exception.OllamaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class OllamaService {
    private static final Logger log = LoggerFactory.getLogger(OllamaService.class);

    private final RestClient restClient;
    private final String endpoint;
    private final String model;

    public OllamaService(
            RestClient.Builder restClientBuilder,
            @Value("${app.ollama.endpoint}") String endpoint,
            @Value("${app.ollama.model}") String model) {
        this.restClient = restClientBuilder.build();
        this.endpoint = endpoint;
        this.model = model;
    }

    public OllamaGenerateResponse generar(String prompt) {
        OllamaGenerateRequest request = new OllamaGenerateRequest(
            model, prompt.trim(), false, Map.of("temperature", 0.1, "top_p", 0.6));

        try {
            OllamaGenerateResponse response = restClient.post()
                .uri(endpoint)
                .body(request)
                .retrieve()
                .body(OllamaGenerateResponse.class);

            if (response == null || response.response() == null) {
                throw new OllamaException("Ollama devolvió una respuesta vacía.");
            }
            return response;
        } catch (OllamaException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.error("No fue posible generar una respuesta con Ollama en {}", endpoint, ex);
            throw new OllamaException("No fue posible comunicarse con el servicio de Ollama.");
        }
    }
}
