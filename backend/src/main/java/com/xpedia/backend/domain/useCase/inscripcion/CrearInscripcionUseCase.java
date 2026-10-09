package com.xpedia.backend.domain.useCase.inscripcion;

import com.xpedia.backend.domain.dto.inscripcion.CrearInscripcionRequest;
import com.xpedia.backend.domain.dto.inscripcion.InscripcionItem;
import com.xpedia.backend.domain.mapper.inscripcion.CrearInscripcionMapper;
import com.xpedia.backend.domain.model.inscripcion.RecorridoPersonal;
import com.xpedia.backend.domain.service.inscripcion.InscripcionService;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
public class CrearInscripcionUseCase {

    private final InscripcionService inscripcionService;

    private final CrearInscripcionMapper crearInscripcionMapper;

    private final Clock clock;

    public InscripcionItem execute(CrearInscripcionRequest request) {
        OffsetDateTime fecha = OffsetDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
        RecorridoPersonal recorrido = inscripcionService.crear(
                request.usuarioId(), request.rutaId(), request.objetivo(), request.metaPersonal(),
                request.ritmoMin(), fecha);
        return crearInscripcionMapper.toResponse(recorrido);
    }
}

