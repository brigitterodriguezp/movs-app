package com.movsapp.backend.controller;
import com.movsapp.backend.dto.*; import com.movsapp.backend.service.PeliculaService;
import com.movsapp.backend.security.RequireRole;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.net.URI; import java.util.List;
@RestController @RequestMapping("/api/peliculas") @RequiredArgsConstructor @Tag(name="Películas")
public class PeliculaController {
 private final PeliculaService service;
 @GetMapping @Operation(summary="Lista películas") public List<PeliculaResponse> listar(){return service.listar();}
 @GetMapping("/{id}") @Operation(summary="Obtiene una película") public PeliculaResponse obtener(@PathVariable Long id){return service.obtener(id);}
 @GetMapping("/genero/{genero}") @Operation(summary="Filtra películas por género") public List<PeliculaResponse> porGenero(@PathVariable String genero){return service.porGenero(genero);}
 @GetMapping("/buscar") @Operation(summary="Busca películas por título") public List<PeliculaResponse> buscar(@RequestParam String titulo){return service.buscar(titulo);}
 @RequireRole("admin")
 @PostMapping @Operation(summary="Crea una película") public ResponseEntity<PeliculaResponse> crear(@Valid @RequestBody PeliculaRequest r){PeliculaResponse x=service.crear(r);return ResponseEntity.created(URI.create("/api/peliculas/"+x.id())).body(x);}
 @RequireRole("admin")
 @PutMapping("/{id}") @Operation(summary="Actualiza una película") public PeliculaResponse actualizar(@PathVariable Long id,@Valid @RequestBody PeliculaRequest r){return service.actualizar(id,r);}
 @RequireRole("admin")
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary="Elimina una película") public void eliminar(@PathVariable Long id){service.eliminar(id);}
}
