package com.xpedia.backend.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "rubrica")
public class RubricaEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "organizacion_id") private UUID organizacionId;
    @Column(nullable = false, columnDefinition = "text") private String nombre;
    @Column(columnDefinition = "text") private String descripcion;
    @Column(name = "puntaje_aprobacion", nullable = false, precision = 5, scale = 2) private BigDecimal puntajeAprobacion;
}
