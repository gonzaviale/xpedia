package com.xpedia.backend.infrastructure.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "progreso_nodo")
@IdClass(ProgresoNodoId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoNodoEntity {

    @Id
    @Column(name = "inscripcion_id")
    private UUID inscripcionId;

    @Id
    @Column(name = "nodo_id")
    private UUID nodoId;

    @Column(name = "estado")
    private String estado;

    @Column(name = "dominio")
    private BigDecimal dominio;

    @Column(name = "nivel")
    private Short nivel;

    @Column(name = "cantidad_fallos")
    private Integer cantidadFallos;

    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}

