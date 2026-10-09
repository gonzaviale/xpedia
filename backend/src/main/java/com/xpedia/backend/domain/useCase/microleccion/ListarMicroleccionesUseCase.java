package com.xpedia.backend.domain.useCase.microleccion;

import com.xpedia.backend.domain.dto.microleccion.*;
import com.xpedia.backend.domain.mapper.microleccion.ListarMicroleccionesMapper;
import com.xpedia.backend.domain.service.microleccion.MicroleccionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarMicroleccionesUseCase {
    private final MicroleccionService service;
    private final ListarMicroleccionesMapper mapper;

    public ListarMicroleccionesResponse execute(ListarMicroleccionesRequest request) {
        return mapper.toResponse(service.listar(request.rutaId(), request.nodoId()));
    }
}
