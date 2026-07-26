package com.movsapp.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OllamaPromptRequest(
    @NotBlank(message = "El prompt es obligatorio.")
    @Size(max = 4000, message = "El prompt no puede superar los 4000 caracteres.")
    String prompt
) {}
