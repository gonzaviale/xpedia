package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.infrastructure.repository.entity.NodoEntity;
import com.xpedia.backend.infrastructure.repository.entity.NodoPrerrequisitoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoPrerrequisitoJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.NodoRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NodoRepositoryImpl implements NodoRepository {
    private final INodoJpaRepository jpa;
    private final INodoPrerrequisitoJpaRepository prerrequisitos;
    private final NodoRepositoryMapper mapper;

    @Override
    public List<Nodo> findByRutaId(UUID rutaId, UUID hitoId) {
        List<NodoEntity> nodos = jpa.findByRutaId(rutaId, hitoId);
        if (nodos.isEmpty()) {
            return List.of();
        }
        List<UUID> ids = nodos.stream().map(NodoEntity::getId).toList();
        Map<UUID, List<UUID>> porNodo = prerrequisitos.findDeNodosEnRuta(ids, rutaId).stream()
                .collect(Collectors.groupingBy(NodoPrerrequisitoEntity::getNodoId,
                        Collectors.mapping(NodoPrerrequisitoEntity::getPrerrequisitoId, Collectors.toList())));
        Map<UUID, INodoJpaRepository.ReferenciasVisibles> referencias = jpa.findReferenciasVisibles(rutaId, ids)
                .stream().collect(Collectors.toMap(INodoJpaRepository.ReferenciasVisibles::getNodoId, r -> r));
        return nodos.stream().map(n -> {
            var referencia = referencias.get(n.getId());
            return mapper.toDomain(n, porNodo.getOrDefault(n.getId(), List.of()),
                    referencia == null ? null : referencia.getRamaId(),
                    referencia == null ? null : referencia.getHabilidadId());
        }).toList();
    }
}
