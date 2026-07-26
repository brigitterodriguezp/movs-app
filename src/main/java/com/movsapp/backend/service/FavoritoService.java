package com.movsapp.backend.service;

import com.movsapp.backend.dto.PeliculaResponse;
import com.movsapp.backend.dto.PaginaResponse;
import com.movsapp.backend.entity.Favorito;
import com.movsapp.backend.entity.FavoritoId;
import com.movsapp.backend.entity.Pelicula;
import com.movsapp.backend.exception.ConflictoException;
import com.movsapp.backend.exception.RecursoNoEncontradoException;
import com.movsapp.backend.repository.FavoritoRepository;
import com.movsapp.backend.repository.PeliculaRepository;
import com.movsapp.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service @RequiredArgsConstructor
public class FavoritoService {
    private final FavoritoRepository favoritos;
    private final UsuarioRepository usuarios;
    private final PeliculaRepository peliculas;

    @Transactional(readOnly = true)
    public List<PeliculaResponse> listar(Long usuarioId) {
        return favoritos.findByUsuarioIdOrderByFechaAgregadaDesc(usuarioId).stream()
            .map(Favorito::getPelicula).map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public PaginaResponse<PeliculaResponse> paginar(Long usuarioId, int pagina, String busqueda) {
        PageRequest pageable = PageRequest.of(Math.max(0, pagina), 5);
        return PaginaResponse.desde(
            favoritos.buscarPagina(usuarioId, busqueda == null ? "" : busqueda.trim(), pageable),
            favorito -> response(favorito.getPelicula()));
    }

    @Transactional
    public PeliculaResponse agregar(Long usuarioId, Long peliculaId) {
        FavoritoId id = new FavoritoId(usuarioId, peliculaId);
        if (favoritos.existsById(id)) throw new ConflictoException("La película ya está en favoritos.");
        var usuario = usuarios.findById(usuarioId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
        var pelicula = peliculas.findById(peliculaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada."));
        favoritos.save(new Favorito(usuario, pelicula));
        return response(pelicula);
    }

    @Transactional
    public void quitar(Long usuarioId, Long peliculaId) {
        FavoritoId id = new FavoritoId(usuarioId, peliculaId);
        if (!favoritos.existsById(id)) throw new RecursoNoEncontradoException("Favorito no encontrado.");
        favoritos.deleteById(id);
    }

    private PeliculaResponse response(Pelicula p) {
        return new PeliculaResponse(p.getId(), p.getTitulo(), p.getAnio(), p.getGenero(), p.getDescripcion(), p.getImagenUrl(), p.getVariante(), p.getActualizadaEn());
    }
}
