package com.xpedia.backend.infrastructure.repository.entity;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inscripcion")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "ruta_id")
    private UUID rutaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "objetivo", columnDefinition = "text")
    private ObjetivoRuta objetivo;

    @Column(name = "meta_personal")
    private String metaPersonal;

    @Column(name = "ritmo_min")
    private Short ritmoMin;

    @Column(name = "fecha_llegada_estimada")
    private LocalDate fechaLlegadaEstimada;

    @Column(name = "estado")
    private String estado;

    @Column(name = "hito_actual_id")
    private UUID hitoActualId;

    @Column(name = "iniciada_en")
    private OffsetDateTime iniciadaEn;

    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}

