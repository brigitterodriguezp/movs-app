package com.movsapp.backend.service;

import com.movsapp.backend.dto.*;
import com.movsapp.backend.entity.Pelicula;
import com.movsapp.backend.exception.RecursoNoEncontradoException;
import com.movsapp.backend.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class PeliculaService {
    private static final Map<String, String> CAMPOS_ORDEN = Map.of(
        "actualizacion", "actualizadaEn",
        "anio", "anio",
        "titulo", "titulo",
        "genero", "genero"
    );
    private static final Map<String, Long> CATEGORIAS = Map.ofEntries(
        Map.entry("Drama", 1L),
        Map.entry("Thriller", 2L),
        Map.entry("Horror", 3L),
        Map.entry("Crime", 4L),
        Map.entry("Mystery", 5L),
        Map.entry("History", 6L),
        Map.entry("Romance", 7L),
        Map.entry("Comedy", 8L),
        Map.entry("Animation", 8L),
        Map.entry("Family", 8L),
        Map.entry("Fantasy", 8L),
        Map.entry("Music", 8L),
        Map.entry("Action", 9L),
        Map.entry("Adventure", 9L),
        Map.entry("War", 9L),
        Map.entry("Western", 9L),
        Map.entry("Science Fiction", 10L),
        Map.entry("Documentary", 11L)
    );
    private final PeliculaRepository repository;
    @Transactional(readOnly=true) public List<PeliculaResponse> listar() { return map(repository.findAll()); }
    @Transactional(readOnly=true) public PaginaResponse<PeliculaResponse> paginar(int pagina, String busqueda, String orden, String direccion) {
        String campo = CAMPOS_ORDEN.getOrDefault(limpiar(orden).toLowerCase(), "actualizadaEn");
        Sort.Direction sentido = "asc".equalsIgnoreCase(limpiar(direccion)) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageable = PageRequest.of(Math.max(0, pagina), 5,
            Sort.by(sentido, campo).and(Sort.by(Sort.Direction.ASC, "id")));
        return PaginaResponse.desde(repository.buscarPagina(limpiar(busqueda), pageable), this::response);
    }
    @Transactional(readOnly=true) public PeliculaResponse obtener(Long id) { return response(entidad(id)); }
    @Transactional(readOnly=true) public List<PeliculaResponse> porGenero(String genero) { return map(repository.findByGeneroIgnoreCase(genero)); }
    @Transactional(readOnly=true) public List<PeliculaResponse> buscar(String titulo) { return map(repository.findByTituloContainingIgnoreCase(titulo)); }
    @Transactional public PeliculaResponse crear(PeliculaRequest r) { Pelicula p = new Pelicula(); copiar(r,p); return response(repository.save(p)); }
    @Transactional public PeliculaResponse actualizar(Long id, PeliculaRequest r) { Pelicula p=entidad(id); copiar(r,p); return response(p); }
    @Transactional public void eliminar(Long id) { repository.delete(entidad(id)); }
    private Pelicula entidad(Long id) { return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada.")); }
    private void copiar(PeliculaRequest r, Pelicula p) { String genero = r.genero().trim(); p.setTitulo(r.titulo().trim()); p.setAnio(r.anio()); p.setGenero(genero); p.setDescripcion(r.descripcion().trim()); p.setImagenUrl(r.imagenUrl().trim()); p.setVariante(r.variante()); p.setCategoriaId(CATEGORIAS.getOrDefault(genero, 1L)); }
    private List<PeliculaResponse> map(List<Pelicula> lista) { return lista.stream().map(this::response).toList(); }
    private String limpiar(String value) { return value == null ? "" : value.trim(); }
    private PeliculaResponse response(Pelicula p) { return new PeliculaResponse(p.getId(),p.getTitulo(),p.getAnio(),p.getGenero(),p.getDescripcion(),p.getImagenUrl(),p.getVariante(),p.getActualizadaEn()); }
}
