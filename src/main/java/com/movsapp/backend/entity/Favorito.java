package com.movsapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos", indexes = @Index(name = "idx_favoritos_pelicula", columnList = "pelicula_id"))
@Getter @Setter @NoArgsConstructor
public class Favorito {
    @EmbeddedId
    private FavoritoId id;

    @MapsId("usuarioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @MapsId("peliculaId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pelicula_id", nullable = false)
    private Pelicula pelicula;

    @Column(name = "fecha_agregada", nullable = false)
    private LocalDateTime fechaAgregada;

    public Favorito(Usuario usuario, Pelicula pelicula) {
        this.id = new FavoritoId(usuario.getId(), pelicula.getId());
        this.usuario = usuario;
        this.pelicula = pelicula;
        this.fechaAgregada = LocalDateTime.now();
    }
}
