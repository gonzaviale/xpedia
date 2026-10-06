package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.CrearPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoResponse;
import com.xpedia.backend.domain.mapper.puesto.CrearPuestoMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrearPuestoUseCase {

    private final PuestoService puestoService;
    private final CrearPuestoMapper mapper;

    public CrearPuestoResponse execute(CrearPuestoRequest request) {
        Puesto creado = puestoService.crear(mapper.toModel(request));
        return mapper.toResponse(creado);
    }
}
