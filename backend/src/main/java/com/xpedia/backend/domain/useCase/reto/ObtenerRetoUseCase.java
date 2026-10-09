package com.xpedia.backend.domain.useCase.reto;

import com.xpedia.backend.domain.dto.reto.ObtenerRetoRequest;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoResponse;
import com.xpedia.backend.domain.mapper.reto.ObtenerRetoMapper;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.service.reto.RetoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObtenerRetoUseCase {

    private final RetoService retoService;
    private final ObtenerRetoMapper mapper;

    public ObtenerRetoResponse execute(ObtenerRetoRequest request) {
        Reto reto = retoService.obtener(request.rutaId(), request.nodoId(), request.retoId());
        return mapper.toResponse(reto);
    }
}
