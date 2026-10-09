package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.rubrica.*;
import com.xpedia.backend.domain.repository.rubrica.RubricaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.*;
import com.xpedia.backend.infrastructure.repository.mapper.RubricaRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component @RequiredArgsConstructor
public class RubricaRepositoryImpl implements RubricaRepository {
    private final IRubricaJpaRepository rubricas;
    private final IRubricaCriterioJpaRepository criterios;
    private final RubricaRepositoryMapper mapper;
    public Map<UUID, Rubrica> findGlobalesByIds(List<UUID> ids) {
        if (ids.isEmpty()) return Map.of();
        var items = rubricas.findGlobalesByIds(ids);
        if (items.isEmpty()) return Map.of();
        var visibles = items.stream().map(r -> r.getId()).toList();
        Map<UUID, List<CriterioRubrica>> porRubrica = criterios.findGlobalesByRubricaIds(visibles).stream()
                .collect(Collectors.groupingBy(c -> c.getRubricaId(), Collectors.mapping(mapper::toCriterio, Collectors.toList())));
        return items.stream().collect(Collectors.toMap(r -> r.getId(),
                r -> mapper.toDomain(r, porRubrica.getOrDefault(r.getId(), List.of()))));
    }
}
