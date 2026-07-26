package com.movsapp.backend.service;

import com.movsapp.backend.exception.RecursoNoEncontradoException;
import com.movsapp.backend.dto.PeliculaLookupResponse;
import com.movsapp.backend.dto.PeliculaMetadataResponse;
import com.movsapp.backend.repository.PeliculaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PeliculaPosterService {
    private static final Logger log = LoggerFactory.getLogger(PeliculaPosterService.class);
    private static final Pattern META_PROPERTY = Pattern.compile(
        "<meta[^>]+property=[\\\"']%s[\\\"'][^>]+content=[\\\"']([^\\\"']*)[\\\"']",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern META_PROPERTY_REVERSED = Pattern.compile(
        "<meta[^>]+content=[\\\"']([^\\\"']*)[\\\"'][^>]+property=[\\\"']%s[\\\"']",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern MOVIE_LINK = Pattern.compile(
        "href=[\\\"'](/movie/(\\d+)(?:-[^?\\\"']*)?)(?:\\?[^\\\"']*)?[\\\"']",
        Pattern.CASE_INSENSITIVE);

    private final PeliculaRepository peliculas;
    private final RestClient.Builder restClientBuilder;
    private final ConcurrentHashMap<Long, PeliculaMetadataResponse> cache = new ConcurrentHashMap<>();

    public Optional<URI> obtenerPoster(Long peliculaId) {
        String imagenUrl = peliculas.findById(peliculaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada."))
            .getImagenUrl();
        if (esImagenTmdb(imagenUrl)) return Optional.of(URI.create(imagenUrl));
        return obtenerMetadata(peliculaId).map(metadata -> URI.create(metadata.posterUrl()));
    }

    public PeliculaLookupResponse buscarPorTitulo(String titulo) {
        try {
            URI busqueda = UriComponentsBuilder.fromUriString("https://www.themoviedb.org/search/movie")
                .queryParam("query", titulo.trim())
                .queryParam("language", "es-ES")
                .build()
                .encode()
                .toUri();
            String html = obtenerHtml(busqueda);
            var match = MOVIE_LINK.matcher(html == null ? "" : html);
            if (!match.find()) {
                throw new RecursoNoEncontradoException("No encontré una película con ese título.");
            }

            String fichaUrl = "https://www.themoviedb.org/movie/" + match.group(2);
            PeliculaMetadataResponse metadata = extraerMetadata(obtenerHtml(URI.create(fichaUrl + "?language=es-ES")))
                .orElseThrow(() -> new RecursoNoEncontradoException("No pude obtener la sinopsis y el póster de esa película."));
            return new PeliculaLookupResponse(
                metadata.titulo(), metadata.descripcion(), metadata.posterUrl(), fichaUrl);
        } catch (RecursoNoEncontradoException ex) {
            throw ex;
        } catch (RestClientException | IllegalArgumentException ex) {
            log.warn("No se pudo buscar la película '{}' en TMDb", titulo);
            throw new RecursoNoEncontradoException("No pude consultar la información de esa película ahora mismo.");
        }
    }

    public Optional<PeliculaMetadataResponse> obtenerMetadata(Long peliculaId) {
        PeliculaMetadataResponse cached = cache.get(peliculaId);
        if (cached != null) return Optional.of(cached);

        String fichaUrl = peliculas.findById(peliculaId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Película no encontrada."))
            .getImagenUrl();
        if (!esUrlTmdb(fichaUrl)) return Optional.empty();

        try {
            String html = obtenerHtml(URI.create(fichaUrl + (fichaUrl.contains("?") ? "&" : "?") + "language=es-ES"));
            Optional<PeliculaMetadataResponse> metadata = extraerMetadata(html);
            metadata.ifPresent(value -> cache.put(peliculaId, value));
            return metadata;
        } catch (RestClientException | IllegalArgumentException ex) {
            log.warn("No se pudo resolver el póster TMDb para peliculaId={}", peliculaId);
            return Optional.empty();
        }
    }

    private boolean esUrlTmdb(String value) {
        try {
            URI uri = URI.create(value);
            String host = uri.getHost();
            return "https".equalsIgnoreCase(uri.getScheme()) && host != null
                && (host.equalsIgnoreCase("themoviedb.org") || host.endsWith(".themoviedb.org")
                    || host.equalsIgnoreCase("image.tmdb.org"));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private boolean esImagenTmdb(String value) {
        if (!esUrlTmdb(value)) return false;
        try {
            String path = URI.create(value).getPath().toLowerCase();
            return path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".webp");
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private String obtenerHtml(URI uri) {
        return restClientBuilder.build().get()
            .uri(uri)
            .accept(MediaType.TEXT_HTML)
            .header("User-Agent", "MovsApp/1.0")
            .retrieve()
            .body(String.class);
    }

    private Optional<PeliculaMetadataResponse> extraerMetadata(String html) {
        if (html == null || html.isBlank()) return Optional.empty();
        String titulo = meta(html, "og:title").orElse("");
        String descripcion = meta(html, "og:description").orElse("");
        String poster = meta(html, "og:image").orElse("");
        if (titulo.isBlank() || descripcion.isBlank() || !esUrlTmdb(poster)) return Optional.empty();
        return Optional.of(new PeliculaMetadataResponse(titulo, descripcion, poster));
    }

    private Optional<String> meta(String html, String property) {
        var match = Pattern.compile(META_PROPERTY.pattern().formatted(Pattern.quote(property)),
            Pattern.CASE_INSENSITIVE).matcher(html);
        if (!match.find()) {
            match = Pattern.compile(META_PROPERTY_REVERSED.pattern().formatted(Pattern.quote(property)),
                Pattern.CASE_INSENSITIVE).matcher(html);
            if (!match.find()) return Optional.empty();
        }
        return Optional.of(HtmlUtils.htmlUnescape(match.group(1).trim()));
    }
}
