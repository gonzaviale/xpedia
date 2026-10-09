package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.repository.cuestionario.CuestionarioRepository;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.entity.PreguntaEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IPreguntaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.CuestionarioRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CuestionarioRepositoryImpl implements CuestionarioRepository {

    private final IActividadJpaRepository actividades;
    private final IPreguntaJpaRepository preguntas;
    private final CuestionarioRepositoryMapper mapper;

    @Override
    public List<Cuestionario> findAprobadosByNodo(UUID rutaId, UUID nodoId) {
        return toDomain(actividades.findCuestionariosAprobados(rutaId, nodoId));
    }

    @Override
    public Optional<Cuestionario> findAprobadoById(UUID actividadId) {
        return actividades.findCuestionarioAprobadoById(actividadId)
                .flatMap(actividad -> toDomain(List.of(actividad)).stream().findFirst());
    }

    private List<Cuestionario> toDomain(List<ActividadEntity> cuestionarios) {
        if (cuestionarios.isEmpty()) {
            return List.of();
        }
        List<UUID> actividadIds = cuestionarios.stream().map(ActividadEntity::getId).toList();
        Map<UUID, List<PreguntaEntity>> preguntasPorActividad = preguntas
                .findByActividadIdInOrderByActividadIdAscPosicionAsc(actividadIds)
                .stream()
                .collect(Collectors.groupingBy(PreguntaEntity::getActividadId));
        return cuestionarios.stream()
                .map(cuestionario -> mapper.toDomain(
                        cuestionario,
                        preguntasPorActividad.getOrDefault(cuestionario.getId(), List.of())))
                .toList();
    }
}
