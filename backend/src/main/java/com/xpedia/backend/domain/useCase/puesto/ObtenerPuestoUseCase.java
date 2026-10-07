package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoResponse;
import com.xpedia.backend.domain.mapper.puesto.ObtenerPuestoMapper;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObtenerPuestoUseCase {

    private final PuestoService puestoService;
    private final ObtenerPuestoMapper mapper;

    public ObtenerPuestoResponse execute(ObtenerPuestoRequest request) {
        return mapper.toResponse(puestoService.obtener(request.id()));
    }
}
