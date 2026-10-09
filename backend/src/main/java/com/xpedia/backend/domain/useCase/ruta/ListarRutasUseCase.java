package com.xpedia.backend.domain.useCase.ruta;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.mapper.ruta.ListarRutasMapper;
import com.xpedia.backend.domain.service.ruta.RutaService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarRutasUseCase {
    private final RutaService rutaService;
    private final ListarRutasMapper mapper;

    public ListarRutasResponse execute(ListarRutasRequest request) {
        return mapper.toResponse(rutaService.listar(request.tipo(), request.objetivo(), mapper.toPageable(request)));
    }
}
