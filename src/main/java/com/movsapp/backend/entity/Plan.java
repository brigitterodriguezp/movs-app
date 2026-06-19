package com.movsapp.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planes", uniqueConstraints = {
    @UniqueConstraint(name = "uk_planes_codigo", columnNames = "codigo"),
    @UniqueConstraint(name = "uk_planes_nombre", columnNames = "nombre")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String codigo;

    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String nombre;

    @NotNull @DecimalMin("0.0") @Digits(integer = 8, fraction = 2)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull @Positive
    @Column(name = "duracion_dias", nullable = false)
    private Integer duracionDias;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "plan_beneficios", joinColumns = @JoinColumn(name = "plan_id"),
        foreignKey = @ForeignKey(name = "fk_beneficios_planes"))
    @Column(name = "beneficio", nullable = false, length = 180)
    @OrderColumn(name = "orden")
    @Builder.Default
    private List<String> beneficios = new ArrayList<>();
}
