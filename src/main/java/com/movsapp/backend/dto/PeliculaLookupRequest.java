package com.movsapp.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PeliculaLookupRequest(
    @NotBlank(message = "El título es obligatorio.")
    @Size(max = 160, message = "El título no puede superar 160 caracteres.")
    String titulo
) {}
