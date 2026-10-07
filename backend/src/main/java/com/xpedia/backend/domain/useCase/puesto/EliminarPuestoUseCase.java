package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.EliminarPuestoRequest;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EliminarPuestoUseCase {

    private final PuestoService puestoService;

    public void execute(EliminarPuestoRequest request) {
        puestoService.eliminar(request.id());
    }
}
