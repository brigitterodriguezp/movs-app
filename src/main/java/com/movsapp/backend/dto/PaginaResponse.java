package com.movsapp.backend.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PaginaResponse<T>(
    List<T> contenido,
    long totalElementos,
    int totalPaginas,
    int pagina,
    int tamano
) {
    public static <S, T> PaginaResponse<T> desde(Page<S> page, Function<S, T> mapper) {
        return new PaginaResponse<>(page.getContent().stream().map(mapper).toList(),
            page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }
}
