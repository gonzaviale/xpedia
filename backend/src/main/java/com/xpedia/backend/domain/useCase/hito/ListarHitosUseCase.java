package com.xpedia.backend.domain.useCase.hito;

import com.xpedia.backend.domain.dto.hito.ListarHitosRequest;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.domain.mapper.hito.ListarHitosMapper;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.service.hito.HitoService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListarHitosUseCase {

    private final HitoService hitoService;
    private final ListarHitosMapper mapper;

    public ListarHitosResponse execute(ListarHitosRequest request) {
        List<Hito> hitos = hitoService.listar(request.rutaId());
        return mapper.toResponse(hitos);
    }
}
