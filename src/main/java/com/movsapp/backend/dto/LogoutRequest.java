package com.movsapp.backend.dto;
import jakarta.validation.constraints.NotNull;
public record LogoutRequest(@NotNull Long idUsuario) {}
