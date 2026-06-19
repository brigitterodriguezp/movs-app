package com.movsapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones", uniqueConstraints = @UniqueConstraint(name = "uk_sesiones_usuario", columnNames = "usuario_id"),
       indexes = @Index(name = "idx_sesiones_activa", columnList = "activa"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sesion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sesiones_usuarios"))
    private Usuario usuario;

    @Column(nullable = false)
    private boolean activa;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
}
