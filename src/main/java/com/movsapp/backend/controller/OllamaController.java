package com.movsapp.backend.controller;

import com.movsapp.backend.dto.OllamaGenerateResponse;
import com.movsapp.backend.dto.OllamaPromptRequest;
import com.movsapp.backend.service.OllamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ollama")
@RequiredArgsConstructor
@Tag(name = "Ollama")
public class OllamaController {
    private final OllamaService service;

    @PostMapping("/generate")
    @Operation(summary = "Genera una respuesta usando el modelo configurado en Ollama")
    public OllamaGenerateResponse generar(@Valid @RequestBody OllamaPromptRequest request) {
        return service.generar(request.prompt());
    }
}
