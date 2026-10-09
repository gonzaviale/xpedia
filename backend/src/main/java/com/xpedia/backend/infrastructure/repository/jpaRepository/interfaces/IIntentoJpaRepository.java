package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.IntentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IIntentoJpaRepository extends JpaRepository<IntentoEntity, UUID> {
}
