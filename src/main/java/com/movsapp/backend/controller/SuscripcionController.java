package com.movsapp.backend.controller;
import com.movsapp.backend.dto.*; import com.movsapp.backend.service.SuscripcionService;
import com.movsapp.backend.security.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.net.URI; import java.util.List;
@RestController @RequestMapping("/api/suscripciones") @RequiredArgsConstructor @Tag(name="Suscripciones")
public class SuscripcionController {
 private final SuscripcionService service;
 @RequireRole("admin")
 @GetMapping @Operation(summary="Lista suscripciones") public List<SuscripcionResponse> listar(){return service.listar();}
 @RequireRole("admin")
 @GetMapping("/{id}") @Operation(summary="Obtiene una suscripción") public SuscripcionResponse obtener(@PathVariable Long id){return service.obtener(id);}
 @GetMapping("/usuario/{idUsuario}") @Operation(summary="Obtiene la suscripción de un usuario") public SuscripcionResponse porUsuario(@PathVariable Long idUsuario){SecurityContext.requireSelfOrAdmin(idUsuario);return service.porUsuario(idUsuario);}
 @RequireRole("admin")
 @PostMapping @Operation(summary="Crea una suscripción") public ResponseEntity<SuscripcionResponse> crear(@Valid @RequestBody SuscripcionRequest r){SuscripcionResponse x=service.crear(r);return ResponseEntity.created(URI.create("/api/suscripciones/"+x.id())).body(x);}
 @RequireRole("admin")
 @PutMapping("/{id}") @Operation(summary="Actualiza una suscripción") public SuscripcionResponse actualizar(@PathVariable Long id,@Valid @RequestBody SuscripcionRequest r){return service.actualizar(id,r);}
 @RequireRole("admin")
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary="Elimina una suscripción") public void eliminar(@PathVariable Long id){service.eliminar(id);}
}
