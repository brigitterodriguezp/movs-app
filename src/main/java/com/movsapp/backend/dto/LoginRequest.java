package com.movsapp.backend.dto;
import jakarta.validation.constraints.*;
public record LoginRequest(@NotBlank @Email String correo, @NotBlank @Size(min=8, max=72) String password) {}
