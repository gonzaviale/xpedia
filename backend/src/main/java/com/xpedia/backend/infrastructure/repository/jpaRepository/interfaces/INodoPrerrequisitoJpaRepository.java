package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.NodoPrerrequisitoEntity;
import com.xpedia.backend.infrastructure.repository.entity.NodoPrerrequisitoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface INodoPrerrequisitoJpaRepository
        extends JpaRepository<NodoPrerrequisitoEntity, NodoPrerrequisitoId> {

    @Query("""
            SELECT p FROM NodoPrerrequisitoEntity p
            JOIN NodoEntity requerido ON requerido.id = p.prerrequisitoId
            LEFT JOIN HitoEntity h ON h.id = requerido.hitoId AND h.rutaId = requerido.rutaId
            WHERE p.nodoId IN :nodoIds AND requerido.rutaId = :rutaId
              AND (requerido.hitoId IS NULL OR h.id IS NOT NULL)
            ORDER BY p.nodoId, p.prerrequisitoId
            """)
    List<NodoPrerrequisitoEntity> findDeNodosEnRuta(@Param("nodoIds") List<UUID> nodoIds,
                                                    @Param("rutaId") UUID rutaId);
}
