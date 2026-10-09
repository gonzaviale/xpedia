package com.xpedia.backend.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "respuesta")
public class RespuestaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "intento_id", nullable = false)
    private UUID intentoId;

    @Column(name = "pregunta_id", nullable = false)
    private UUID preguntaId;

    @Column(nullable = false)
    private Short elegida;

    @Column(nullable = false)
    private Boolean correcta;

    @Column(columnDefinition = "text")
    private String confianza;

    private Integer milisegundos;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = OffsetDateTime.now();
    }
}
