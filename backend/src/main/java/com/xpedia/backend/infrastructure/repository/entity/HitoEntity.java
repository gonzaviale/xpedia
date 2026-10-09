package com.xpedia.backend.infrastructure.repository.entity;

import lombok.*;
import java.util.UUID;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import com.xpedia.backend.domain.model.enums.EstadoPropuestaHito;
import jakarta.persistence.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hito")
public class HitoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "ruta_id", nullable = false)
    private UUID rutaId;

    @Column(name = "posicion", nullable = false)
    private Short posicion;

    @Column(name = "titulo", nullable = false, columnDefinition = "text")
    private String titulo;

    @Column(name = "objetivo", columnDefinition = "text")
    private String objetivo;

    @Column(name = "horas_estimadas", precision = 6, scale = 1)
    private BigDecimal horasEstimadas;

    @Column(name = "es_final", nullable = false)
    private Boolean esFinal;

    @Column(name = "evidencia_esperada", columnDefinition = "text")
    private String evidenciaEsperada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_propuesta", nullable = false, columnDefinition = "text")
    private EstadoPropuestaHito estadoPropuesta;

    @Column(name = "comentario_ajuste", columnDefinition = "text")
    private String comentarioAjuste;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
}
