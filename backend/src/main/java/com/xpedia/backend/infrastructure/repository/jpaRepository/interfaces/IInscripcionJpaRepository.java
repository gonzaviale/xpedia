package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.InscripcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IInscripcionJpaRepository extends JpaRepository<InscripcionEntity, UUID> {

    /** ACTIVA y PAUSADA cuentan como abiertas, igual que el índice único de V1. */
    @Query("""
            SELECT COUNT(i) > 0 FROM InscripcionEntity i
            WHERE i.usuarioId = :usuarioId AND i.rutaId = :rutaId AND i.estado <> 'TERMINADA'
            """)
    boolean existsAbiertaByUsuarioIdAndRutaId(UUID usuarioId, UUID rutaId);

    Optional<InscripcionEntity> findFirstByUsuarioIdAndEstadoOrderByIniciadaEnDescIdDesc(UUID usuarioId, String estado);
}

