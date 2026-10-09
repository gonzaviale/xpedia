package com.xpedia.backend.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "intento")
public class IntentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "inscripcion_id")
    private UUID inscripcionId;

    @Column(name = "actividad_id", nullable = false)
    private UUID actividadId;

    @Column(nullable = false, columnDefinition = "text")
    private String modo;

    private BigDecimal puntaje;

    private Boolean aprobado;

    @Column(name = "iniciado_en", nullable = false)
    private OffsetDateTime iniciadoEn;

    @Column(name = "terminado_en")
    private OffsetDateTime terminadoEn;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = actualizadoEn = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }
}
