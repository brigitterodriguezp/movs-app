package com.movsapp.backend.controller;
import com.movsapp.backend.dto.*; import com.movsapp.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.responses.*; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor @Tag(name="Autenticación")
public class AuthController {
 private final AuthService service;
 @PostMapping("/login") @Operation(summary="Inicia una sesión",description="Devuelve 409 si el usuario ya tiene una sesión activa.") @ApiResponse(responseCode="401",description="Credenciales incorrectas") @ApiResponse(responseCode="409",description="Sesión activa")
 public SesionResponse login(@Valid @RequestBody LoginRequest r){return service.login(r);}
 @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary="Cierra una sesión") public void logout(@Valid @RequestBody LogoutRequest r){service.logout(r.idUsuario());}
 @GetMapping("/sesion/{idUsuario}") @Operation(summary="Consulta la sesión de un usuario") public SesionResponse sesion(@PathVariable Long idUsuario){return service.sesion(idUsuario);}
}
