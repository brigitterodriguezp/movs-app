package com.movsapp.backend.dto;
import java.math.BigDecimal;
import java.util.List;
public record PlanResponse(Long id, String codigo, String nombre, BigDecimal precio, Integer duracionDias, List<String> beneficios) {}
