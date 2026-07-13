package com.movsapp.backend.controller;

import com.movsapp.backend.dto.RegistroRequest;
import com.movsapp.backend.dto.RegistroResponse;
import com.movsapp.backend.service.RegistroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "Registro")
public class RegistroController {
    private final RegistroService service;

    @PostMapping("/api/registro")
    @SecurityRequirements
    @Operation(summary = "Registra un usuario USER y crea su suscripción")
    public ResponseEntity<RegistroResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        RegistroResponse response = service.registrar(request);
        return ResponseEntity.created(URI.create("/api/usuarios/" + response.usuario().id())).body(response);
    }
}
