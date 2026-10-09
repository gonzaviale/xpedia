package com.xpedia.backend.domain.useCase.hito;

import com.xpedia.backend.domain.dto.hito.ListarHitosRequest;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.domain.mapper.hito.ListarHitosMapper;
import com.xpedia.backend.domain.service.hito.HitoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarHitosUseCase {
    private final HitoService service;
    private final ListarHitosMapper mapper;

    public ListarHitosResponse execute(ListarHitosRequest request) {
        return mapper.toResponse(service.listar(request.rutaId()));
    }
}
