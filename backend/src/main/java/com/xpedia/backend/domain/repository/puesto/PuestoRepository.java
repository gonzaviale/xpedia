package com.xpedia.backend.domain.repository.puesto;

import com.xpedia.backend.domain.model.puesto.Puesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PuestoRepository {

    Puesto save(Puesto puesto);

    Optional<Puesto> findById(UUID id);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    boolean existsByOrganizacionIdAndNombre(UUID organizacionId, String nombre);

    boolean existsByOrganizacionIdAndNombreAndIdNot(UUID organizacionId, String nombre, UUID id);

    Page<Puesto> findAll(UUID organizacionId, Pageable pageable);
}
