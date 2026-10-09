package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.repository.intento.IntentoRepository;
import com.xpedia.backend.infrastructure.repository.entity.IntentoEntity;
import com.xpedia.backend.infrastructure.repository.entity.RespuestaEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IIntentoJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRespuestaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.IntentoRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IntentoRepositoryImpl implements IntentoRepository {

    private final IIntentoJpaRepository intentos;
    private final IRespuestaJpaRepository respuestas;
    private final IntentoRepositoryMapper mapper;

    @Override
    public Intento save(Intento intento) {
        IntentoEntity guardado = intentos.save(mapper.toEntity(intento));
        List<RespuestaEntity> entidades = intento.getRespuestas().stream()
                .map(respuesta -> mapper.toEntity(respuesta, guardado.getId()))
                .toList();
        respuestas.saveAll(entidades);
        return mapper.toDomain(guardado, intento.getRespuestas());
    }
}
