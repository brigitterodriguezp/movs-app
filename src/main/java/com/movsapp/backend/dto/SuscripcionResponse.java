package com.movsapp.backend.dto;
import com.movsapp.backend.entity.EstadoSuscripcion;
import java.time.LocalDate;
public record SuscripcionResponse(Long id, Long usuarioId, Long planId, String plan, LocalDate fechaInicio,
                                  LocalDate fechaExpiracion, EstadoSuscripcion estado) {}
