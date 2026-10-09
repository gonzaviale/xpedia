package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;
import com.xpedia.backend.infrastructure.repository.entity.RubricaCriterioEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface IRubricaCriterioJpaRepository extends JpaRepository<RubricaCriterioEntity, UUID> {
    @Query("""
            SELECT c FROM RubricaCriterioEntity c JOIN RubricaEntity r ON r.id = c.rubricaId
            WHERE c.rubricaId IN (:ids) AND r.organizacionId IS NULL ORDER BY c.posicion, c.id
            """)
    List<RubricaCriterioEntity> findGlobalesByRubricaIds(@Param("ids") List<UUID> ids);
}
