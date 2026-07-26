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

@Service @RequiredArgsConstructor
public class PeliculaService {
    private final PeliculaRepository repository;
    @Transactional(readOnly=true) public List<PeliculaResponse> listar() { return map(repository.findAll()); }
    @Transactional(readOnly=true) public PaginaResponse<PeliculaResponse> paginar(int pagina, String busqueda) {
        PageRequest pageable = PageRequest.of(Math.max(0, pagina), 5, Sort.by("id").ascending());
        return PaginaResponse.desde(repository.buscarPagina(limpiar(busqueda), pageable), this::response);
    }
    @Transactional(readOnly=true) public PeliculaResponse obtener(Long id) { return response(entidad(id)); }
    @Transactional(readOnly=true) public List<PeliculaResponse> porGenero(String genero) { return map(repository.findByGeneroIgnoreCase(genero)); }
    @Transactional(readOnly=true) public List<PeliculaResponse> buscar(String titulo) { return map(repository.findByTituloContainingIgnoreCase(titulo)); }
    @Transactional public PeliculaResponse crear(PeliculaRequest r) { Pelicula p = new Pelicula(); copiar(r,p); return response(repository.save(p)); }
    @Transactional public PeliculaResponse actualizar(Long id, PeliculaRequest r) { Pelicula p=entidad(id); copiar(r,p); return response(p); }
    @Transactional public void eliminar(Long id) { repository.delete(entidad(id)); }
    private Pelicula entidad(Long id) { return repository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada.")); }
    private void copiar(PeliculaRequest r, Pelicula p) { p.setTitulo(r.titulo().trim()); p.setAnio(r.anio()); p.setGenero(r.genero().trim()); p.setDescripcion(r.descripcion().trim()); p.setImagenUrl(r.imagenUrl().trim()); p.setVariante(r.variante()); }
    private List<PeliculaResponse> map(List<Pelicula> lista) { return lista.stream().map(this::response).toList(); }
    private String limpiar(String value) { return value == null ? "" : value.trim(); }
    private PeliculaResponse response(Pelicula p) { return new PeliculaResponse(p.getId(),p.getTitulo(),p.getAnio(),p.getGenero(),p.getDescripcion(),p.getImagenUrl(),p.getVariante()); }
}
