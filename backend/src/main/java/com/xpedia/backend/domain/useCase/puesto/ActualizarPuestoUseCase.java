package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoResponse;
import com.xpedia.backend.domain.mapper.puesto.ActualizarPuestoMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActualizarPuestoUseCase {

    private final PuestoService puestoService;
    private final ActualizarPuestoMapper mapper;

    public ActualizarPuestoResponse execute(ActualizarPuestoRequest request) {
        Puesto actualizado = puestoService.actualizar(request.id(), mapper.toModel(request));
        return mapper.toResponse(actualizado);
    }
}
