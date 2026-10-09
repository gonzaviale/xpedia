package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.infrastructure.repository.entity.RutaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IRutaJpaRepository extends JpaRepository<RutaEntity, UUID> {

    @Query("""
            SELECT r FROM RutaEntity r
            WHERE r.organizacionId IS NULL
              AND r.estado = com.xpedia.backend.domain.model.enums.EstadoRuta.PUBLICADA
              AND (:tipo IS NULL OR r.tipo = :tipo)
              AND (:objetivo IS NULL OR r.objetivo = :objetivo)
            """)
    Page<RutaEntity> findPublicadasGlobales(@Param("tipo") TipoRuta tipo,
                                            @Param("objetivo") ObjetivoRuta objetivo,
                                            Pageable pageable);

    @Query("""
            SELECT r FROM RutaEntity r
            WHERE r.id = :id AND r.organizacionId IS NULL
              AND r.estado = com.xpedia.backend.domain.model.enums.EstadoRuta.PUBLICADA
            """)
    Optional<RutaEntity> findPublicadaGlobalById(@Param("id") UUID id);

    @Query("""
            SELECT COUNT(r) > 0 FROM RutaEntity r
            WHERE r.id = :id AND r.organizacionId IS NULL
              AND r.estado = com.xpedia.backend.domain.model.enums.EstadoRuta.PUBLICADA
            """)
    boolean existsPublicadaGlobalById(@Param("id") UUID id);
}
