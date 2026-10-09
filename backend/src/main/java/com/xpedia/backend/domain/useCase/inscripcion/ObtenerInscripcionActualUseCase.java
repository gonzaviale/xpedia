package com.xpedia.backend.domain.useCase.inscripcion;

import com.xpedia.backend.domain.dto.inscripcion.InscripcionItem;
import com.xpedia.backend.domain.dto.inscripcion.ObtenerInscripcionActualRequest;
import com.xpedia.backend.domain.mapper.inscripcion.ObtenerInscripcionActualMapper;
import com.xpedia.backend.domain.model.inscripcion.RecorridoPersonal;
import com.xpedia.backend.domain.service.inscripcion.InscripcionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObtenerInscripcionActualUseCase {

    private final InscripcionService inscripcionService;

    private final ObtenerInscripcionActualMapper obtenerInscripcionActualMapper;

    public InscripcionItem execute(ObtenerInscripcionActualRequest request) {
        RecorridoPersonal recorrido = inscripcionService.obtenerActual(request.usuarioId());
        return obtenerInscripcionActualMapper.toResponse(recorrido);
    }
}

