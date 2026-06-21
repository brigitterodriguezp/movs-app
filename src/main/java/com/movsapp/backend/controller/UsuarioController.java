package com.movsapp.backend.controller;

import com.movsapp.backend.dto.*;
import com.movsapp.backend.security.*;
import com.movsapp.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController @RequestMapping("/api/usuarios") @RequiredArgsConstructor @Tag(name="Usuarios")
@RequireRole("admin")
public class UsuarioController {
    private final UsuarioService service;
    @GetMapping("/me") @RequireRole({"admin","usuario"}) @Operation(summary="Obtiene el perfil del usuario autenticado")
    public UsuarioResponse perfil(){ return service.obtener(SecurityContext.current().id()); }

    @GetMapping @Operation(summary="Lista usuarios") public List<UsuarioResponse> listar(){ return service.listar(); }
    @GetMapping("/{id}") @Operation(summary="Obtiene un usuario") public UsuarioResponse obtener(@PathVariable Long id){ return service.obtener(id); }
    @GetMapping("/rol/{rol}") @Operation(summary="Filtra usuarios por rol") public List<UsuarioResponse> porRol(@PathVariable String rol){ return service.porRol(rol); }
    @PostMapping @Operation(summary="Crea un usuario") @ApiResponse(responseCode="201",description="Usuario creado")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest r){ UsuarioResponse x=service.crear(r); return ResponseEntity.created(URI.create("/api/usuarios/"+x.id())).body(x); }
    @PutMapping("/{id}") @Operation(summary="Actualiza un usuario") public UsuarioResponse actualizar(@PathVariable Long id,@Valid @RequestBody UsuarioRequest r){ return service.actualizar(id,r); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary="Elimina un usuario") public void eliminar(@PathVariable Long id){ service.eliminar(id); }
}
