package com.xpedia.backend.domain.useCase.ruta;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.mapper.ruta.ListarRutasMapper;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.service.ruta.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@RequiredArgsConstructor
public class ListarRutasUseCase {

    private final RutaService rutaService;
    private final ListarRutasMapper mapper;

    public ListarRutasResponse execute(ListarRutasRequest request) {
        Page<Ruta> rutas = rutaService.listar(request.tipo(), request.objetivo(), mapper.toPageable(request));
        return mapper.toResponse(rutas);
    }
}
