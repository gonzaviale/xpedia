package com.xpedia.backend.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "actividad")
public class ActividadEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "ruta_id", nullable = false) private UUID rutaId;
    @Column(name = "nodo_id") private UUID nodoId;
    @Column(name = "hito_id") private UUID hitoId;
    @Column(name = "rubrica_id") private UUID rubricaId;
    @Column(nullable = false, columnDefinition = "text") private String tipo;
    @Column(nullable = false, columnDefinition = "text") private String titulo;
    @Column(nullable = false) private Short nivel;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false) private Map<String, Object> contenido;
    @Column(nullable = false, columnDefinition = "text") private String origen;
    @Column(name = "estado_revision", nullable = false, columnDefinition = "text") private String estadoRevision;
    @Column(name = "revisado_en") private OffsetDateTime revisadoEn;
    @Column(name = "creado_en", nullable = false) private OffsetDateTime creadoEn;
    @Column(name = "actualizado_en", nullable = false) private OffsetDateTime actualizadoEn;
}
