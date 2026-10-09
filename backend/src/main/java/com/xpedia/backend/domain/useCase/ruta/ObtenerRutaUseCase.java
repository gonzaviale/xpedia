package com.xpedia.backend.domain.useCase.ruta;

import com.xpedia.backend.domain.dto.ruta.ObtenerRutaRequest;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaResponse;
import com.xpedia.backend.domain.mapper.ruta.ObtenerRutaMapper;
import com.xpedia.backend.domain.service.ruta.RutaService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObtenerRutaUseCase {
    private final RutaService rutaService;
    private final ObtenerRutaMapper mapper;

    public ObtenerRutaResponse execute(ObtenerRutaRequest request) {
        return mapper.toResponse(rutaService.obtener(request.id()));
    }
}
