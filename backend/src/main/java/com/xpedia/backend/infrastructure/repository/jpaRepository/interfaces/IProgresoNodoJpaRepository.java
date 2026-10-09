package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.ProgresoNodoEntity;
import com.xpedia.backend.infrastructure.repository.entity.ProgresoNodoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IProgresoNodoJpaRepository extends JpaRepository<ProgresoNodoEntity, ProgresoNodoId> {

    /** Conserva el orden del catálogo y nunca mezcla progresos de otra inscripción. */
    @Query("""
            SELECT p FROM ProgresoNodoEntity p
            JOIN NodoEntity n ON n.id = p.nodoId
            LEFT JOIN HitoEntity h ON h.id = n.hitoId AND h.rutaId = n.rutaId
            WHERE p.inscripcionId = :inscripcionId
            ORDER BY h.posicion ASC NULLS LAST, n.posicion ASC, n.id ASC
            """)
    List<ProgresoNodoEntity> findByInscripcionIdOrdenado(UUID inscripcionId);
}

