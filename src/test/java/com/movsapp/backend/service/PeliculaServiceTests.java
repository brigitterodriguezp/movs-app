package com.movsapp.backend.service;

import com.movsapp.backend.dto.PeliculaRequest;
import com.movsapp.backend.entity.Pelicula;
import com.movsapp.backend.repository.PeliculaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PeliculaServiceTests {
    @Test
    void ordenaPorAnioAntesDePaginar() {
        PeliculaRepository repository = mock(PeliculaRepository.class);
        when(repository.buscarPagina(anyString(), any(Pageable.class))).thenReturn(Page.empty());
        PeliculaService service = new PeliculaService(repository);

        service.paginar(0, "", "anio", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).buscarPagina(anyString(), captor.capture());
        assertEquals("anio: DESC,id: ASC", captor.getValue().getSort().toString());
    }

    @Test
    void ordenaPorUltimaActualizacionAntesDePaginar() {
        PeliculaRepository repository = mock(PeliculaRepository.class);
        when(repository.buscarPagina(anyString(), any(Pageable.class))).thenReturn(Page.empty());
        PeliculaService service = new PeliculaService(repository);

        service.paginar(0, "", "actualizacion", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).buscarPagina(anyString(), captor.capture());
        assertEquals("actualizadaEn: DESC,id: ASC", captor.getValue().getSort().toString());
    }

    @Test
    void asignaCategoriaAntesDeCrearLaPelicula() {
        PeliculaRepository repository = mock(PeliculaRepository.class);
        when(repository.save(any(Pelicula.class))).thenAnswer(invocation -> {
            Pelicula pelicula = invocation.getArgument(0);
            pelicula.setId(1001L);
            return pelicula;
        });
        PeliculaService service = new PeliculaService(repository);
        PeliculaRequest request = new PeliculaRequest(
            "Hotel Transilvania", 2012, "Animation",
            "Drácula regenta un hotel para monstruos.",
            "https://media.themoviedb.org/t/p/w500/poster.jpg",
            "movie-card-wide");

        service.crear(request);

        ArgumentCaptor<Pelicula> captor = ArgumentCaptor.forClass(Pelicula.class);
        verify(repository).save(captor.capture());
        assertEquals(8L, captor.getValue().getCategoriaId());
    }
}
