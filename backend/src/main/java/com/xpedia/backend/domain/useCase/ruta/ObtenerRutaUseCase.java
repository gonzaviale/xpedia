package com.xpedia.backend.domain.useCase.ruta;

import com.xpedia.backend.domain.dto.ruta.ObtenerRutaRequest;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaResponse;
import com.xpedia.backend.domain.mapper.ruta.ObtenerRutaMapper;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.service.ruta.RutaService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObtenerRutaUseCase {

    private final RutaService rutaService;
    private final ObtenerRutaMapper mapper;

    public ObtenerRutaResponse execute(ObtenerRutaRequest request) {
        Ruta ruta = rutaService.obtener(request.id());
        return mapper.toResponse(ruta);
    }
}
