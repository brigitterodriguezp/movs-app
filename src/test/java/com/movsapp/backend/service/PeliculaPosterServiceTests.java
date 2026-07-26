package com.movsapp.backend.service;

import com.movsapp.backend.repository.PeliculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PeliculaPosterServiceTests {
    private static final MediaType HTML_UTF8 = MediaType.parseMediaType("text/html;charset=UTF-8");
    private MockRestServiceServer server;
    private PeliculaPosterService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        service = new PeliculaPosterService(mock(PeliculaRepository.class), builder);
    }

    @Test
    void completaTodosLosCamposDesdeElTitulo() {
        server.expect(once(), requestTo(
                "https://www.themoviedb.org/search/movie?query=Hotel%20Transilvania&language=es-ES"))
            .andRespond(withSuccess("""
                <a href="/movie/76492-hotel-transylvania?language=es-ES">Hotel Transilvania</a>
                """, HTML_UTF8));
        server.expect(once(), requestTo("https://www.themoviedb.org/movie/76492?language=es-ES"))
            .andRespond(withSuccess("""
                <meta property="og:title" content="Hotel Transilvania">
                <meta property="og:description" content="Drácula regenta un hotel para monstruos.">
                <meta property="og:image" content="https://media.themoviedb.org/t/p/w500/poster.jpg">
                <span class="release">28/9/2012 (US)</span>
                <span class="genres"><a href="/genre/16-animation/movie">Animación</a></span>
                """, HTML_UTF8));

        var result = service.buscarPorTitulo("Hotel Transilvania");

        assertEquals("Hotel Transilvania", result.titulo());
        assertEquals(2012, result.anio());
        assertEquals("Animation", result.genero());
        assertEquals("movie-card-wide", result.variante());
        assertEquals("Drácula regenta un hotel para monstruos.", result.descripcion());
        assertEquals("https://media.themoviedb.org/t/p/w500/poster.jpg", result.posterUrl());
        server.verify();
    }
}
