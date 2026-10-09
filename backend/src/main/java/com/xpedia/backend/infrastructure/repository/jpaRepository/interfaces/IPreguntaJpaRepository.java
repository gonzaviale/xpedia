package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.PreguntaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPreguntaJpaRepository extends JpaRepository<PreguntaEntity, UUID> {

    List<PreguntaEntity> findByActividadIdInOrderByActividadIdAscPosicionAsc(Collection<UUID> actividadIds);
}
