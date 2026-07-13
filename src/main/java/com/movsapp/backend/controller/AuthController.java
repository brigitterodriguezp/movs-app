package com.movsapp.backend.controller;
import com.movsapp.backend.dto.*; import com.movsapp.backend.service.AuthService;
import com.movsapp.backend.security.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.responses.*; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import org.slf4j.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor @Tag(name="Autenticación")
public class AuthController {
 private static final Logger log = LoggerFactory.getLogger(AuthController.class);
 private final AuthService service;
 private final LoginRateLimitService rateLimit;
 @PostMapping("/login") @io.swagger.v3.oas.annotations.security.SecurityRequirements @Operation(summary="Inicia una sesión",description="Devuelve 409 si el usuario ya tiene una sesión activa.") @ApiResponse(responseCode="401",description="Credenciales incorrectas") @ApiResponse(responseCode="409",description="Sesión activa")
 public SesionResponse login(@Valid @RequestBody LoginRequest r, HttpServletRequest request){
  String key=request.getRemoteAddr()+":"+r.correo().trim().toLowerCase();
  rateLimit.check(key);
  try {
   SesionResponse response=service.login(r);
   rateLimit.reset(key);
   log.info("Inicio de sesión exitoso para usuarioId={} desde {}", response.usuarioId(), request.getRemoteAddr());
   return response;
  } catch (RuntimeException ex) {
   log.warn("Intento de inicio de sesión fallido desde {}", request.getRemoteAddr());
   throw ex;
  }
 }
 @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary="Cierra la sesión autenticada") public void logout(){Long id=SecurityContext.current().id();service.logout(id);log.info("Cierre de sesión exitoso para usuarioId={}", id);}
 @GetMapping("/sesion/{idUsuario}") @Operation(summary="Consulta la sesión de un usuario") public SesionResponse sesion(@PathVariable Long idUsuario){SecurityContext.requireSelfOrAdmin(idUsuario);return service.sesion(idUsuario);}
}
