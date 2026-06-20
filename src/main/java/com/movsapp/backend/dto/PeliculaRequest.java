package com.movsapp.backend.dto;
import jakarta.validation.constraints.*;
public record PeliculaRequest(
    @NotBlank @Size(max=160) String titulo,
    @NotNull @Min(1888) @Max(2100) Integer anio,
    @NotBlank @Size(max=60) String genero,
    @NotBlank @Size(max=1000) String descripcion,
    @NotBlank @Size(max=255) String imagenUrl,
    @Size(max=60) String variante
) {}
