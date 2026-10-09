package com.xpedia.backend.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pregunta")
public class PreguntaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "actividad_id", nullable = false)
    private UUID actividadId;

    @Column(nullable = false)
    private Short posicion;

    @Column(nullable = false, columnDefinition = "text")
    private String tipo;

    @Column(nullable = false, columnDefinition = "text")
    private String enunciado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private List<String> opciones;

    @Column(nullable = false)
    private Short correcta;

    @Column(columnDefinition = "text")
    private String explicacion;
}
