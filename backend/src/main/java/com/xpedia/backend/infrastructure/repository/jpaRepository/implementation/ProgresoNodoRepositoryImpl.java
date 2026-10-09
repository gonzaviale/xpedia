package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.inscripcion.ProgresoNodo;
import com.xpedia.backend.domain.repository.inscripcion.ProgresoNodoRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IProgresoNodoJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.ProgresoNodoRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProgresoNodoRepositoryImpl implements ProgresoNodoRepository {

    private final IProgresoNodoJpaRepository progresoNodoJpaRepository;

    private final ProgresoNodoRepositoryMapper progresoNodoRepositoryMapper;

    @Override
    public void saveAll(List<ProgresoNodo> progreso) {
        progresoNodoJpaRepository.saveAllAndFlush(progreso.stream()
                .map(progresoNodoRepositoryMapper::toEntity)
                .toList());
    }

    @Override
    public List<ProgresoNodo> findByInscripcionId(UUID inscripcionId) {
        return progresoNodoJpaRepository.findByInscripcionIdOrdenado(inscripcionId).stream()
                .map(progresoNodoRepositoryMapper::toDomain)
                .toList();
    }
}

