package com.movsapp.backend.dto;
import java.time.LocalDateTime;
public record SesionResponse(Long id, Long usuarioId, String correo, String rol, boolean activa,
                             LocalDateTime fechaInicio, LocalDateTime fechaCierre) {}
