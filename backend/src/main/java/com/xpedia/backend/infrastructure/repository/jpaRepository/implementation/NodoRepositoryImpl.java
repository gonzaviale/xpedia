package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.infrastructure.repository.entity.NodoEntity;
import com.xpedia.backend.infrastructure.repository.entity.NodoPrerrequisitoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoJpaRepository.ReferenciasVisibles;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoPrerrequisitoJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.NodoRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NodoRepositoryImpl implements NodoRepository {

    private final INodoJpaRepository jpa;
    private final INodoPrerrequisitoJpaRepository prerrequisitos;
    private final NodoRepositoryMapper mapper;

    @Override
    public boolean existsVisibleByIdAndRutaId(UUID id, UUID rutaId) {
        return jpa.existsVisibleByIdAndRutaId(id, rutaId);
    }

    @Override
    public List<Nodo> findVisiblesByRutaIdAndHitoId(UUID rutaId, UUID hitoId) {
        List<NodoEntity> nodos = jpa.findByRutaId(rutaId, hitoId);
        if (nodos.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = nodos.stream().map(NodoEntity::getId).toList();
        Map<UUID, List<UUID>> prerrequisitosPorNodo = agruparPrerrequisitos(rutaId, ids);
        Map<UUID, ReferenciasVisibles> referenciasPorNodo = agruparReferencias(rutaId, ids);
        return nodos.stream()
                .map(nodo -> toDomain(nodo, prerrequisitosPorNodo, referenciasPorNodo))
                .toList();
    }

    private Map<UUID, List<UUID>> agruparPrerrequisitos(UUID rutaId, List<UUID> ids) {
        return prerrequisitos.findDeNodosEnRuta(ids, rutaId).stream()
                .collect(Collectors.groupingBy(
                        NodoPrerrequisitoEntity::getNodoId,
                        Collectors.mapping(NodoPrerrequisitoEntity::getPrerrequisitoId, Collectors.toList())));
    }

    private Map<UUID, ReferenciasVisibles> agruparReferencias(UUID rutaId, List<UUID> ids) {
        return jpa.findReferenciasVisibles(rutaId, ids).stream()
                .collect(Collectors.toMap(ReferenciasVisibles::getNodoId, Function.identity()));
    }

    private Nodo toDomain(
            NodoEntity nodo,
            Map<UUID, List<UUID>> prerrequisitosPorNodo,
            Map<UUID, ReferenciasVisibles> referenciasPorNodo) {
        ReferenciasVisibles referencia = referenciasPorNodo.get(nodo.getId());
        UUID ramaId = referencia == null ? null : referencia.getRamaId();
        UUID habilidadId = referencia == null ? null : referencia.getHabilidadId();
        List<UUID> prerrequisitoIds = prerrequisitosPorNodo.getOrDefault(nodo.getId(), List.of());
        return mapper.toDomain(nodo, prerrequisitoIds, ramaId, habilidadId);
    }
}
