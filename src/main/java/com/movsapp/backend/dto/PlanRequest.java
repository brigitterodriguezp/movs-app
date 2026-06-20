package com.movsapp.backend.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
public record PlanRequest(
    @NotBlank @Size(max=30) String codigo,
    @NotBlank @Size(max=80) String nombre,
    @NotNull @DecimalMin("0.0") @Digits(integer=8, fraction=2) BigDecimal precio,
    @NotNull @Positive Integer duracionDias,
    @NotNull @Size(min=1) List<@NotBlank @Size(max=180) String> beneficios
) {}
