package com.xpedia.backend.domain.useCase.intento;

import com.xpedia.backend.domain.dto.intento.RegistrarIntentoRequest;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoResponse;
import com.xpedia.backend.domain.mapper.intento.RegistrarIntentoMapper;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import com.xpedia.backend.domain.service.intento.IntentoService;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class RegistrarIntentoUseCase {

    private final IntentoService intentoService;
    private final RegistrarIntentoMapper mapper;
    private final Clock clock;

    public RegistrarIntentoResponse execute(RegistrarIntentoRequest request) {
        List<Respuesta> respuestas = mapper.toRespuestas(request.respuestas());
        Intento intento = intentoService.registrar(
                request.usuarioId(),
                request.inscripcionId(),
                request.actividadId(),
                respuestas,
                request.iniciadoEn(),
                OffsetDateTime.now(clock));
        return mapper.toResponse(intento);
    }
}
