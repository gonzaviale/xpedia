package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.RespuestaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IRespuestaJpaRepository extends JpaRepository<RespuestaEntity, UUID> {
}
