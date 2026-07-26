package com.movsapp.backend.dto;
import java.time.OffsetDateTime;
public record PeliculaResponse(Long id, String titulo, Integer anio, String genero, String descripcion,
                               String imagenUrl, String variante, OffsetDateTime actualizadaEn) {}
