package com.xpedia.backend.domain.useCase.microleccion;

import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesRequest;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesResponse;
import com.xpedia.backend.domain.mapper.microleccion.ListarMicroleccionesMapper;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.domain.service.microleccion.MicroleccionService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListarMicroleccionesUseCase {

    private final MicroleccionService microleccionService;
    private final ListarMicroleccionesMapper mapper;

    public ListarMicroleccionesResponse execute(ListarMicroleccionesRequest request) {
        List<Microleccion> microlecciones = microleccionService.listar(request.rutaId(), request.nodoId());
        return mapper.toResponse(microlecciones);
    }
}
