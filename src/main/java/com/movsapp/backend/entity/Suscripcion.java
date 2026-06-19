package com.movsapp.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "suscripciones", uniqueConstraints = @UniqueConstraint(name = "uk_suscripciones_usuario", columnNames = "usuario_id"),
       indexes = @Index(name = "idx_suscripciones_plan", columnList = "plan_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Suscripcion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_suscripciones_usuarios"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_suscripciones_planes"))
    private Plan plan;

    @NotNull @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull @Column(name = "fecha_expiracion", nullable = false)
    private LocalDate fechaExpiracion;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSuscripcion estado;
}
