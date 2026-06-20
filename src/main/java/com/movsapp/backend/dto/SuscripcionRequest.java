package com.movsapp.backend.dto;
import com.movsapp.backend.entity.EstadoSuscripcion;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record SuscripcionRequest(
    @NotNull Long usuarioId,
    @NotNull Long planId,
    LocalDate fechaInicio,
    EstadoSuscripcion estado
) {}
