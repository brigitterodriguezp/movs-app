package com.movsapp.backend.dto;

public record PeliculaLookupResponse(
    String titulo,
    String descripcion,
    String posterUrl,
    String fichaUrl
) {}
