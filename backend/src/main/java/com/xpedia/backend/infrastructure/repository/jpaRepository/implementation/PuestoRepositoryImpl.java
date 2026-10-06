package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.repository.puesto.PuestoRepository;
import com.xpedia.backend.infrastructure.repository.entity.PuestoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IPuestoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PuestoRepositoryImpl implements PuestoRepository {

    private final IPuestoJpaRepository jpa;

    @Override
    public Puesto save(Puesto puesto) {
        PuestoEntity saved = jpa.save(toEntity(puesto));
        return toDomain(saved);
    }

    @Override
    public Optional<Puesto> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpa.existsById(id);
    }

    @Override
    public boolean existsByOrganizacionIdAndNombre(UUID organizacionId, String nombre) {
        return jpa.existsByOrganizacionIdAndNombre(organizacionId, nombre);
    }

    @Override
    public boolean existsByOrganizacionIdAndNombreAndIdNot(UUID organizacionId, String nombre, UUID id) {
        return jpa.existsByOrganizacionIdAndNombreAndIdNot(organizacionId, nombre, id);
    }

    @Override
    public Page<Puesto> findAll(UUID organizacionId, Pageable pageable) {
        return jpa.findByOrganizacionId(organizacionId, pageable).map(this::toDomain);
    }

    private PuestoEntity toEntity(Puesto puesto) {
        return PuestoEntity.builder()
                .id(puesto.getId())
                .organizacionId(puesto.getOrganizacionId())
                .nombre(puesto.getNombre())
                .creadoEn(puesto.getCreadoEn())
                .actualizadoEn(puesto.getActualizadoEn())
                .build();
    }

    private Puesto toDomain(PuestoEntity entity) {
        return Puesto.builder()
                .id(entity.getId())
                .organizacionId(entity.getOrganizacionId())
                .nombre(entity.getNombre())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}
