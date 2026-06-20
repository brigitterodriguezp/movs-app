package com.movsapp.backend.dto;
import jakarta.validation.constraints.*;
public record UsuarioRequest(
    @NotBlank @Size(max=120) String nombre,
    @NotBlank @Email @Size(max=160) String correo,
    @Size(min=8, max=72) String password,
    @NotBlank String rol
) {}
