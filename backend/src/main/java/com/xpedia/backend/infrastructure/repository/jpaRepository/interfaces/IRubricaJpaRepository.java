package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.RubricaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IRubricaJpaRepository extends JpaRepository<RubricaEntity, UUID> {

    @Query("SELECT r FROM RubricaEntity r WHERE r.id IN (:ids) AND r.organizacionId IS NULL")
    List<RubricaEntity> findGlobalesByIds(@Param("ids") List<UUID> ids);
}
