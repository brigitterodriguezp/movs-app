package com.movsapp.backend.controller;
import com.movsapp.backend.dto.*; import com.movsapp.backend.service.PlanService;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.net.URI; import java.util.List;
@RestController @RequestMapping("/api/planes") @RequiredArgsConstructor @Tag(name="Planes")
public class PlanController {
 private final PlanService service;
 @GetMapping @Operation(summary="Lista planes") public List<PlanResponse> listar(){return service.listar();}
 @GetMapping("/{id}") @Operation(summary="Obtiene un plan") public PlanResponse obtener(@PathVariable Long id){return service.obtener(id);}
 @PostMapping @Operation(summary="Crea un plan") public ResponseEntity<PlanResponse> crear(@Valid @RequestBody PlanRequest r){PlanResponse x=service.crear(r);return ResponseEntity.created(URI.create("/api/planes/"+x.id())).body(x);}
 @PutMapping("/{id}") @Operation(summary="Actualiza un plan") public PlanResponse actualizar(@PathVariable Long id,@Valid @RequestBody PlanRequest r){return service.actualizar(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary="Elimina un plan") public void eliminar(@PathVariable Long id){service.eliminar(id);}
}
