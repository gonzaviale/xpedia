package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.HitoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IHitoJpaRepository extends JpaRepository<HitoEntity, UUID> {

    List<HitoEntity> findByRutaIdOrderByPosicionAscIdAsc(UUID rutaId);

    boolean existsByIdAndRutaId(UUID id, UUID rutaId);
}
