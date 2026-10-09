package com.xpedia.backend.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "rubrica_criterio")
public class RubricaCriterioEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "rubrica_id", nullable = false) private UUID rubricaId;
    @Column(nullable = false) private Short posicion;
    @Column(nullable = false, columnDefinition = "text") private String nombre;
    @Column(columnDefinition = "text") private String descripcion;
    @Column(name = "puntaje_max", nullable = false) private Short puntajeMax;
    @Column(nullable = false, precision = 4, scale = 2) private BigDecimal peso;
    @Column(nullable = false) private boolean eliminatorio;
}
