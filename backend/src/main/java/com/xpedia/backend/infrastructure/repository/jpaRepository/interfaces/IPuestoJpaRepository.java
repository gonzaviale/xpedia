package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.PuestoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * En las consultas derivadas, un organizacionId null se traduce a "organizacion_id IS NULL",
 * que es justo el caso de los puestos globales (UNIQUE NULLS NOT DISTINCT en la migración).
 */
public interface IPuestoJpaRepository extends JpaRepository<PuestoEntity, UUID> {

    boolean existsByOrganizacionIdAndNombre(UUID organizacionId, String nombre);

    boolean existsByOrganizacionIdAndNombreAndIdNot(UUID organizacionId, String nombre, UUID id);

    Page<PuestoEntity> findByOrganizacionId(UUID organizacionId, Pageable pageable);
}
