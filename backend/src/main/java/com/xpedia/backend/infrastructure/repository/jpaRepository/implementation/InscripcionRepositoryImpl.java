package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.inscripcion.Inscripcion;
import com.xpedia.backend.domain.repository.inscripcion.InscripcionRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IInscripcionJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.InscripcionRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InscripcionRepositoryImpl implements InscripcionRepository {

    private final IInscripcionJpaRepository inscripcionJpaRepository;

    private final InscripcionRepositoryMapper inscripcionRepositoryMapper;

    @Override
    public boolean existsAbiertaByUsuarioIdAndRutaId(UUID usuarioId, UUID rutaId) {
        return inscripcionJpaRepository.existsAbiertaByUsuarioIdAndRutaId(usuarioId, rutaId);
    }

    @Override
    public Inscripcion save(Inscripcion inscripcion) {
        return inscripcionRepositoryMapper.toDomain(
                inscripcionJpaRepository.saveAndFlush(inscripcionRepositoryMapper.toEntity(inscripcion)));
    }

    @Override
    public Optional<Inscripcion> findActualByUsuarioId(UUID usuarioId) {
        return inscripcionJpaRepository.findFirstByUsuarioIdAndEstadoOrderByIniciadaEnDescIdDesc(usuarioId, "ACTIVA")
                .map(inscripcionRepositoryMapper::toDomain);
    }
}

