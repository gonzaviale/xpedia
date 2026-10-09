package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.repository.ruta.RutaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRutaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RutaRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RutaRepositoryImpl implements RutaRepository {
    private final IRutaJpaRepository jpa;
    private final RutaRepositoryMapper mapper;

    @Override
    public Page<Ruta> findPublicadasGlobales(TipoRuta tipo, ObjetivoRuta objetivo, Pageable pageable) {
        return jpa.findPublicadasGlobales(tipo, objetivo, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<Ruta> findPublicadaGlobalById(UUID id) {
        return jpa.findPublicadaGlobalById(id).map(mapper::toDomain);
    }
}
