package com.xpedia.backend.infrastructure.repository.entity;

import lombok.*;
import java.util.UUID;
import java.time.OffsetDateTime;
import com.xpedia.backend.domain.model.enums.TipoNodo;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nodo")
public class NodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "ruta_id", nullable = false)
    private UUID rutaId;

    @Column(name = "hito_id")
    private UUID hitoId;

    @Column(name = "rama_id")
    private UUID ramaId;

    @Column(name = "habilidad_id")
    private UUID habilidadId;

    @Column(name = "codigo", nullable = false, columnDefinition = "text")
    private String codigo;

    @Column(name = "titulo", nullable = false, columnDefinition = "text")
    private String titulo;

    @Column(name = "resumen", columnDefinition = "text")
    private String resumen;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, columnDefinition = "text")
    private TipoNodo tipo;

    @Column(name = "nivel", nullable = false)
    private Short nivel;

    @Column(name = "minutos_estimados")
    private Short minutosEstimados;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "palabras_clave", nullable = false)
    private String[] palabrasClave;

    @Column(name = "posicion", nullable = false)
    private Short posicion;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;
}
