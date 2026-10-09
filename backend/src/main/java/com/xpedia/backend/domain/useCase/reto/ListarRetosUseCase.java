package com.xpedia.backend.domain.useCase.reto;

import com.xpedia.backend.domain.dto.reto.ListarRetosRequest;
import com.xpedia.backend.domain.dto.reto.ListarRetosResponse;
import com.xpedia.backend.domain.mapper.reto.ListarRetosMapper;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.service.reto.RetoService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListarRetosUseCase {

    private final RetoService retoService;
    private final ListarRetosMapper mapper;

    public ListarRetosResponse execute(ListarRetosRequest request) {
        List<Reto> retos = retoService.listar(request.rutaId(), request.nodoId());
        return mapper.toResponse(retos);
    }
}
