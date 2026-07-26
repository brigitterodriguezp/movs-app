package com.movsapp.backend.dto;

public record PeliculaLookupResponse(
    String titulo,
    Integer anio,
    String genero,
    String variante,
    String descripcion,
    String posterUrl,
    String fichaUrl
) {}
