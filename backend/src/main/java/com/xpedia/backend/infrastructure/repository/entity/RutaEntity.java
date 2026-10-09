package com.xpedia.backend.infrastructure.repository.entity;

import com.xpedia.backend.domain.model.enums.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ruta")
public class RutaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "organizacion_id")
    private UUID organizacionId;

    @Column(name = "slug", nullable = false, columnDefinition = "text")
    private String slug;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "titulo", nullable = false, columnDefinition = "text")
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, columnDefinition = "text")
    private TipoRuta tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "objetivo", nullable = false, columnDefinition = "text")
    private ObjetivoRuta objetivo;

    @Column(name = "meta", nullable = false, columnDefinition = "text")
    private String meta;

    @Column(name = "perfil_inicial", columnDefinition = "text")
    private String perfilInicial;

    @Column(name = "pais", columnDefinition = "text")
    private String pais;

    @Column(name = "horas_estimadas", precision = 6, scale = 1)
    private BigDecimal horasEstimadas;

    @Column(name = "ritmo_recomendado_min", nullable = false)
    private Short ritmoRecomendadoMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, columnDefinition = "text")
    private EstadoRuta estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "validacion", nullable = false, columnDefinition = "text")
    private ValidacionRuta validacion;

    @Column(name = "revisada_por")
    private UUID revisadaPor;

    @Column(name = "revisada_en")
    private OffsetDateTime revisadaEn;

    @Column(name = "confirmada_por")
    private UUID confirmadaPor;

    @Column(name = "confirmada_en")
    private OffsetDateTime confirmadaEn;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
}
