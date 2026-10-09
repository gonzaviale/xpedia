package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.domain.repository.microleccion.MicroleccionRepository;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadFuenteJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadFuenteJpaRepository.FuenteVisible;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.MicroleccionRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MicroleccionRepositoryImpl implements MicroleccionRepository {

    private final IActividadJpaRepository actividades;
    private final IActividadFuenteJpaRepository fuentes;
    private final MicroleccionRepositoryMapper mapper;

    @Override
    public List<Microleccion> findAprobadasByNodo(UUID rutaId, UUID nodoId) {
        List<ActividadEntity> microlecciones = actividades.findMicroleccionesAprobadas(rutaId, nodoId);
        if (microlecciones.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = microlecciones.stream().map(ActividadEntity::getId).toList();
        Map<UUID, List<FuenteMicroleccion>> fuentesPorActividad = agruparFuentes(ids);
        return microlecciones.stream()
                .map(microleccion -> mapper.toDomain(
                        microleccion,
                        fuentesPorActividad.getOrDefault(microleccion.getId(), List.of())))
                .toList();
    }

    private Map<UUID, List<FuenteMicroleccion>> agruparFuentes(List<UUID> actividadIds) {
        return fuentes.findVisiblesDeActividades(actividadIds).stream()
                .collect(Collectors.groupingBy(
                        FuenteVisible::getActividadId,
                        Collectors.mapping(mapper::toFuente, Collectors.toList())));
    }
}
