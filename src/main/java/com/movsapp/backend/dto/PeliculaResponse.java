package com.movsapp.backend.dto;
public record PeliculaResponse(Long id, String titulo, Integer anio, String genero, String descripcion,
                               String imagenUrl, String variante) {}
