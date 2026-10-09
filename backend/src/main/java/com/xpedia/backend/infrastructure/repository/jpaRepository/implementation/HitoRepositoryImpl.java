package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.repository.hito.HitoRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IHitoJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.HitoRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HitoRepositoryImpl implements HitoRepository {
    private final IHitoJpaRepository jpa;
    private final HitoRepositoryMapper mapper;

    @Override
    public List<Hito> findByRutaId(UUID rutaId) {
        return jpa.findByRutaIdOrderByPosicionAscIdAsc(rutaId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByIdAndRutaId(UUID id, UUID rutaId) {
        return jpa.existsByIdAndRutaId(id, rutaId);
    }
}
