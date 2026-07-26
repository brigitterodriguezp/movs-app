package com.movsapp.backend.controller;
import com.movsapp.backend.dto.*; import com.movsapp.backend.service.PeliculaService; import com.movsapp.backend.service.PeliculaPosterService;
import com.movsapp.backend.security.RequireRole;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.net.URI; import java.util.List;
@RestController @RequestMapping("/api/peliculas") @RequiredArgsConstructor @Tag(name="Películas")
public class PeliculaController {
 private final PeliculaService service;
 private final PeliculaPosterService posterService;
 @GetMapping @io.swagger.v3.oas.annotations.security.SecurityRequirements @Operation(summary="Lista películas") public List<PeliculaResponse> listar(){return service.listar();}
 @GetMapping("/pagina") @io.swagger.v3.oas.annotations.security.SecurityRequirements @Operation(summary="Lista películas paginadas, máximo 5 por página") public PaginaResponse<PeliculaResponse> pagina(@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="") String busqueda,@RequestParam(defaultValue="actualizacion") String orden,@RequestParam(defaultValue="desc") String direccion){return service.paginar(pagina,busqueda,orden,direccion);}
 @GetMapping("/{id}") @io.swagger.v3.oas.annotations.security.SecurityRequirements @Operation(summary="Obtiene una película") public PeliculaResponse obtener(@PathVariable Long id){return service.obtener(id);}
 @GetMapping("/{id}/poster") @io.swagger.v3.oas.annotations.security.SecurityRequirements @Operation(summary="Redirige al póster de una película") public ResponseEntity<Void> poster(@PathVariable Long id){return posterService.obtenerPoster(id).map(uri -> ResponseEntity.status(HttpStatus.FOUND).location(uri).<Void>build()).orElseGet(() -> ResponseEntity.notFound().build());}
 @GetMapping("/{id}/metadata") @io.swagger.v3.oas.annotations.security.SecurityRequirements @Operation(summary="Obtiene título, descripción y póster en español") public ResponseEntity<PeliculaMetadataResponse> metadata(@PathVariable Long id){return ResponseEntity.of(posterService.obtenerMetadata(id));}
 @GetMapping("/genero/{genero}") @io.swagger.v3.oas.annotations.security.SecurityRequirements @Operation(summary="Filtra películas por género") public List<PeliculaResponse> porGenero(@PathVariable String genero){return service.porGenero(genero);}
 @GetMapping("/buscar") @io.swagger.v3.oas.annotations.security.SecurityRequirements @Operation(summary="Busca películas por título") public List<PeliculaResponse> buscar(@RequestParam String titulo){return service.buscar(titulo);}
 @RequireRole("admin")
 @PostMapping("/asistente") @Operation(summary="Busca en TMDb la sinopsis en español y el póster por título") public PeliculaLookupResponse asistente(@Valid @RequestBody PeliculaLookupRequest r){return posterService.buscarPorTitulo(r.titulo());}
 @RequireRole("admin")
 @PostMapping @Operation(summary="Crea una película") public ResponseEntity<PeliculaResponse> crear(@Valid @RequestBody PeliculaRequest r){PeliculaResponse x=service.crear(r);return ResponseEntity.created(URI.create("/api/peliculas/"+x.id())).body(x);}
 @RequireRole("admin")
 @PutMapping("/{id}") @Operation(summary="Actualiza una película") public PeliculaResponse actualizar(@PathVariable Long id,@Valid @RequestBody PeliculaRequest r){return service.actualizar(id,r);}
 @RequireRole("admin")
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary="Elimina una película") public void eliminar(@PathVariable Long id){service.eliminar(id);}
}
