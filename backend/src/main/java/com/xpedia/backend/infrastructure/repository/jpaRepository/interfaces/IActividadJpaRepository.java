package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface IActividadJpaRepository extends JpaRepository<ActividadEntity, UUID> {
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
