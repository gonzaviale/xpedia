package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.NodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface INodoJpaRepository extends JpaRepository<NodoEntity, UUID> {

    @Query("""
            SELECT COUNT(n) > 0 FROM NodoEntity n
            LEFT JOIN HitoEntity h ON h.id = n.hitoId AND h.rutaId = n.rutaId
            WHERE n.id = :id AND n.rutaId = :rutaId AND (n.hitoId IS NULL OR h.id IS NOT NULL)
            """)
    boolean existsVisibleByIdAndRutaId(@Param("id") UUID id, @Param("rutaId") UUID rutaId);

    interface ReferenciasVisibles {

        UUID getNodoId();

        UUID getRamaId();

        UUID getHabilidadId();
    }

    @Query(value = """
            SELECT n.id AS nodoId,
                   CASE WHEN r.ruta_id = n.ruta_id THEN n.rama_id END AS ramaId,
                   CASE WHEN h.id IS NOT NULL AND h.organizacion_id IS NULL THEN n.habilidad_id END AS habilidadId
            FROM nodo n
            LEFT JOIN rama r ON r.id = n.rama_id
            LEFT JOIN habilidad h ON h.id = n.habilidad_id
            WHERE n.ruta_id = :rutaId AND n.id IN (:ids)
            """, nativeQuery = true)
    List<ReferenciasVisibles> findReferenciasVisibles(@Param("rutaId") UUID rutaId,
                                                      @Param("ids") List<UUID> ids);

    @Query("""
            SELECT n FROM NodoEntity n
            LEFT JOIN HitoEntity h ON h.id = n.hitoId AND h.rutaId = n.rutaId
            WHERE n.rutaId = :rutaId AND (:hitoId IS NULL OR n.hitoId = :hitoId)
              AND (n.hitoId IS NULL OR h.id IS NOT NULL)
            ORDER BY h.posicion ASC NULLS LAST, n.posicion ASC, n.id ASC
            """)
    List<NodoEntity> findByRutaId(@Param("rutaId") UUID rutaId, @Param("hitoId") UUID hitoId);
}
