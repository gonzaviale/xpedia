package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.rubrica.CriterioRubrica;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import com.xpedia.backend.domain.repository.rubrica.RubricaRepository;
import com.xpedia.backend.infrastructure.repository.entity.RubricaCriterioEntity;
import com.xpedia.backend.infrastructure.repository.entity.RubricaEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRubricaCriterioJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRubricaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RubricaRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RubricaRepositoryImpl implements RubricaRepository {

    private final IRubricaJpaRepository rubricas;
    private final IRubricaCriterioJpaRepository criterios;
    private final RubricaRepositoryMapper mapper;

    @Override
    public Map<UUID, Rubrica> findGlobalesByIds(List<UUID> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<RubricaEntity> rubricasGlobales = rubricas.findGlobalesByIds(ids);
        if (rubricasGlobales.isEmpty()) {
            return Map.of();
        }
        List<UUID> rubricaIds = rubricasGlobales.stream().map(RubricaEntity::getId).toList();
        Map<UUID, List<CriterioRubrica>> criteriosPorRubrica = agruparCriterios(rubricaIds);
        return rubricasGlobales.stream()
                .collect(Collectors.toMap(
                        RubricaEntity::getId,
                        rubrica -> mapper.toDomain(
                                rubrica,
                                criteriosPorRubrica.getOrDefault(rubrica.getId(), List.of()))));
    }

    private Map<UUID, List<CriterioRubrica>> agruparCriterios(List<UUID> rubricaIds) {
        return criterios.findGlobalesByRubricaIds(rubricaIds).stream()
                .collect(Collectors.groupingBy(
                        RubricaCriterioEntity::getRubricaId,
                        Collectors.mapping(mapper::toCriterio, Collectors.toList())));
    }
}
