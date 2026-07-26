package com.movsapp.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "peliculas", indexes = {
    @Index(name = "idx_peliculas_titulo", columnList = "titulo"),
    @Index(name = "idx_peliculas_genero", columnList = "genero")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pelicula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 160)
    @Column(nullable = false, length = 160)
    private String titulo;

    @NotNull @Min(1888) @Max(2100)
    @Column(nullable = false)
    private Integer anio;

    @NotBlank @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String genero;

    @NotBlank @Size(max = 1000)
    @Column(nullable = false, length = 1000)
    private String descripcion;

    @NotBlank @Size(max = 255)
    @Column(name = "imagen_url", nullable = false)
    private String imagenUrl;

    @Size(max = 60)
    @Column(length = 60)
    private String variante;

    @NotNull
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @NotNull
    @Column(name = "actualizada_en", nullable = false)
    private OffsetDateTime actualizadaEn;

    @PrePersist
    void antesDeCrear() {
        actualizadaEn = OffsetDateTime.now();
    }

    @PreUpdate
    void antesDeActualizar() {
        actualizadaEn = OffsetDateTime.now();
    }
}
