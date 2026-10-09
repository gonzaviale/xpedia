package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Reglas de visibilidad pública que implementan las consultas nativas de este repositorio.
 *
 * <p>Comunes a retos y microlecciones: la actividad está APROBADA, pertenece al nodo y a la ruta indicados
 * y no tiene asociada ninguna fuente propia de una organización. Si el nodo tiene hito, ese hito debe existir
 * dentro de la misma ruta, y la actividad solo puede declarar un hito igual al del nodo (o ninguno).
 * El orden es por nivel, fecha de creación e id.</p>
 *
 * <p>Retos (ENSAYO, RETO_PROYECTO y DESAFIO_REAL): la rúbrica es global (sin organización) y válida, o sea,
 * tiene al menos un criterio, todos los criterios tienen peso positivo y el puntaje de aprobación está entre 0
 * y el puntaje máximo (suma de puntaje máximo por peso de cada criterio). Además, {@code contenido.consigna}
 * es un texto con algún carácter que no sea espacio en blanco.</p>
 *
 * <p>Microlecciones (MICROLECCION): tienen al menos una fuente global asociada.</p>
 *
 * <p>Cuestionarios (CUESTIONARIO): tienen al menos una pregunta cargada.</p>
 */
public interface IActividadJpaRepository extends JpaRepository<ActividadEntity, UUID> {

    String RETOS_VISIBLES = """
            SELECT a.* FROM actividad a
            JOIN nodo n ON n.id = a.nodo_id AND n.ruta_id = a.ruta_id
            LEFT JOIN hito h ON h.id = n.hito_id AND h.ruta_id = n.ruta_id
            JOIN rubrica r ON r.id = a.rubrica_id AND r.organizacion_id IS NULL
            WHERE a.ruta_id = :rutaId AND a.nodo_id = :nodoId
              AND a.tipo IN ('ENSAYO', 'RETO_PROYECTO', 'DESAFIO_REAL') AND a.estado_revision = 'APROBADA'
              AND (n.hito_id IS NULL OR h.id IS NOT NULL)
              AND (a.hito_id IS NULL OR a.hito_id = n.hito_id)
              AND jsonb_typeof(a.contenido -> 'consigna') = 'string'
              AND (a.contenido ->> 'consigna') ~ '[^[:space:]]'
              AND EXISTS (SELECT 1 FROM rubrica_criterio c WHERE c.rubrica_id = r.id)
              AND NOT EXISTS (SELECT 1 FROM rubrica_criterio c WHERE c.rubrica_id = r.id AND c.peso <= 0)
              AND r.puntaje_aprobacion BETWEEN 0 AND
                  (SELECT SUM(c.puntaje_max * c.peso) FROM rubrica_criterio c WHERE c.rubrica_id = r.id)
              AND NOT EXISTS (SELECT 1 FROM actividad_fuente af JOIN fuente f ON f.id = af.fuente_id
                              WHERE af.actividad_id = a.id AND f.organizacion_id IS NOT NULL)
            """;

    String CUESTIONARIOS_VISIBLES = """
            SELECT a.* FROM actividad a
            JOIN nodo n ON n.id = a.nodo_id AND n.ruta_id = a.ruta_id
            LEFT JOIN hito h ON h.id = n.hito_id AND h.ruta_id = n.ruta_id
            WHERE a.tipo = 'CUESTIONARIO' AND a.estado_revision = 'APROBADA'
              AND (n.hito_id IS NULL OR h.id IS NOT NULL)
              AND (a.hito_id IS NULL OR a.hito_id = n.hito_id)
              AND EXISTS (SELECT 1 FROM pregunta p WHERE p.actividad_id = a.id)
            """;

    @Query(value = CUESTIONARIOS_VISIBLES + " AND a.ruta_id = :rutaId AND a.nodo_id = :nodoId"
            + " ORDER BY a.nivel, a.creado_en, a.id", nativeQuery = true)
    List<ActividadEntity> findCuestionariosAprobados(@Param("rutaId") UUID rutaId, @Param("nodoId") UUID nodoId);

    @Query(value = CUESTIONARIOS_VISIBLES + " AND a.id = :actividadId", nativeQuery = true)
    Optional<ActividadEntity> findCuestionarioAprobadoById(@Param("actividadId") UUID actividadId);

    @Query(value = RETOS_VISIBLES + " ORDER BY a.nivel, a.creado_en, a.id", nativeQuery = true)
    List<ActividadEntity> findRetosAprobados(@Param("rutaId") UUID rutaId, @Param("nodoId") UUID nodoId);

    @Query(value = RETOS_VISIBLES + " AND a.id = :retoId", nativeQuery = true)
    Optional<ActividadEntity> findRetoAprobadoById(@Param("rutaId") UUID rutaId,
                                                   @Param("nodoId") UUID nodoId,
                                                   @Param("retoId") UUID retoId);

    @Query(value = """
            SELECT a.* FROM actividad a
            JOIN nodo n ON n.id = a.nodo_id AND n.ruta_id = a.ruta_id
            LEFT JOIN hito h ON h.id = n.hito_id AND h.ruta_id = n.ruta_id
            WHERE a.ruta_id = :rutaId AND a.nodo_id = :nodoId
              AND a.tipo = 'MICROLECCION' AND a.estado_revision = 'APROBADA'
              AND (n.hito_id IS NULL OR h.id IS NOT NULL)
              AND (a.hito_id IS NULL OR a.hito_id = n.hito_id)
              AND EXISTS (SELECT 1 FROM actividad_fuente af JOIN fuente f ON f.id = af.fuente_id
                          WHERE af.actividad_id = a.id AND f.organizacion_id IS NULL)
              AND NOT EXISTS (SELECT 1 FROM actividad_fuente af JOIN fuente f ON f.id = af.fuente_id
                              WHERE af.actividad_id = a.id AND f.organizacion_id IS NOT NULL)
            ORDER BY a.nivel, a.creado_en, a.id
            """, nativeQuery = true)
    List<ActividadEntity> findMicroleccionesAprobadas(@Param("rutaId") UUID rutaId, @Param("nodoId") UUID nodoId);
}
