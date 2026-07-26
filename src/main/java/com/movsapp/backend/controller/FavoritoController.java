package com.movsapp.backend.controller;

import com.movsapp.backend.dto.PeliculaResponse;
import com.movsapp.backend.dto.PaginaResponse;
import com.movsapp.backend.security.RequireRole;
import com.movsapp.backend.security.SecurityContext;
import com.movsapp.backend.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/me/favoritos")
@RequiredArgsConstructor
@RequireRole({"admin", "usuario"})
@Tag(name = "Favoritos")
public class FavoritoController {
    private final FavoritoService service;

    @GetMapping
    @Operation(summary = "Lista las películas favoritas del usuario autenticado")
    public List<PeliculaResponse> listar() {
        return service.listar(SecurityContext.current().id());
    }

    @GetMapping("/pagina")
    @Operation(summary = "Lista favoritos paginados, máximo 5 por página")
    public PaginaResponse<PeliculaResponse> pagina(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "") String busqueda) {
        return service.paginar(SecurityContext.current().id(), pagina, busqueda);
    }

    @PostMapping("/{peliculaId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Agrega una película a favoritos")
    public PeliculaResponse agregar(@PathVariable Long peliculaId) {
        return service.agregar(SecurityContext.current().id(), peliculaId);
    }

    @DeleteMapping("/{peliculaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quita una película de favoritos")
    public void quitar(@PathVariable Long peliculaId) {
        service.quitar(SecurityContext.current().id(), peliculaId);
    }
}
