package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import com.xpedia.backend.domain.repository.reto.RetoRepository;
import com.xpedia.backend.domain.repository.rubrica.RubricaRepository;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RetoRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RetoRepositoryImpl implements RetoRepository {

    private final IActividadJpaRepository actividades;
    private final RubricaRepository rubricaRepository;
    private final RetoRepositoryMapper mapper;

    @Override
    public List<Reto> findAprobadosByNodo(UUID rutaId, UUID nodoId) {
        return toDomain(actividades.findRetosAprobados(rutaId, nodoId));
    }

    @Override
    public Optional<Reto> findAprobadoById(UUID rutaId, UUID nodoId, UUID retoId) {
        return actividades.findRetoAprobadoById(rutaId, nodoId, retoId)
                .flatMap(actividad -> toDomain(List.of(actividad)).stream().findFirst());
    }

    private List<Reto> toDomain(List<ActividadEntity> retos) {
        if (retos.isEmpty()) {
            return List.of();
        }
        List<UUID> rubricaIds = retos.stream().map(ActividadEntity::getRubricaId).distinct().toList();
        Map<UUID, Rubrica> rubricasGlobales = rubricaRepository.findGlobalesByIds(rubricaIds);
        return descartarSinRubricaGlobal(retos, rubricasGlobales).stream()
                .map(reto -> mapper.toDomain(reto, rubricasGlobales.get(reto.getRubricaId())))
                .toList();
    }

    private List<ActividadEntity> descartarSinRubricaGlobal(
            List<ActividadEntity> retos,
            Map<UUID, Rubrica> rubricasGlobales) {
        return retos.stream()
                .filter(reto -> rubricasGlobales.containsKey(reto.getRubricaId()))
                .toList();
    }
}
