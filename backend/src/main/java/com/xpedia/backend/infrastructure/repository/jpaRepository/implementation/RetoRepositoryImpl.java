package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.repository.reto.RetoRepository;
import com.xpedia.backend.domain.repository.rubrica.RubricaRepository;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RetoRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

@Component @RequiredArgsConstructor
public class RetoRepositoryImpl implements RetoRepository {
    private final IActividadJpaRepository actividades;
    private final RubricaRepository rubricas;
    private final RetoRepositoryMapper mapper;
    public List<Reto> findAprobadosByNodo(UUID rutaId, UUID nodoId) {
        return mapear(actividades.findRetosAprobados(rutaId, nodoId));
    }
    public Optional<Reto> findAprobadoById(UUID rutaId, UUID nodoId, UUID retoId) {
        return actividades.findRetoAprobadoById(rutaId, nodoId, retoId)
                .flatMap(a -> mapear(List.of(a)).stream().findFirst());
    }
    private List<Reto> mapear(List<ActividadEntity> items) {
        if (items.isEmpty()) return List.of();
        var ids = items.stream().map(a -> a.getRubricaId()).distinct().toList();
        var disponibles = rubricas.findGlobalesByIds(ids);
        return items.stream().filter(a -> disponibles.containsKey(a.getRubricaId()))
                .map(a -> mapper.toDomain(a, disponibles.get(a.getRubricaId()))).toList();
    }
}
