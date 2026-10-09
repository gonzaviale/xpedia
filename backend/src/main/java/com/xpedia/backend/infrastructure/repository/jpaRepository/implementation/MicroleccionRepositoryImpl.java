package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.microleccion.*;
import com.xpedia.backend.domain.repository.microleccion.MicroleccionRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.*;
import com.xpedia.backend.infrastructure.repository.mapper.MicroleccionRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MicroleccionRepositoryImpl implements MicroleccionRepository {
    private final IActividadJpaRepository actividades;
    private final IActividadFuenteJpaRepository fuentes;
    private final MicroleccionRepositoryMapper mapper;

    @Override
    public List<Microleccion> findAprobadasByNodo(UUID rutaId, UUID nodoId) {
        var items = actividades.findMicroleccionesAprobadas(rutaId, nodoId);
        if (items.isEmpty()) return List.of();
        var ids = items.stream().map(a -> a.getId()).toList();
        Map<UUID, List<FuenteMicroleccion>> porActividad = fuentes.findVisiblesDeActividades(ids).stream()
                .collect(Collectors.groupingBy(IActividadFuenteJpaRepository.FuenteVisible::getActividadId,
                        Collectors.mapping(mapper::toFuente, Collectors.toList())));
        return items.stream().map(a -> mapper.toDomain(a, porActividad.getOrDefault(a.getId(), List.of()))).toList();
    }
}
