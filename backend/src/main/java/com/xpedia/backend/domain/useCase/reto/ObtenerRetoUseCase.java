package com.xpedia.backend.domain.useCase.reto;
import com.xpedia.backend.domain.dto.reto.*;
import com.xpedia.backend.domain.mapper.reto.RetoMapper;
import com.xpedia.backend.domain.service.reto.RetoService;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class ObtenerRetoUseCase {
    private final RetoService service;
    private final RetoMapper mapper;
    public ObtenerRetoResponse execute(ObtenerRetoRequest request) {
        return mapper.toObtenerResponse(service.obtener(request.rutaId(), request.nodoId(), request.retoId()));
    }
}
