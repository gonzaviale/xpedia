package com.xpedia.backend.domain.useCase.reto;
import com.xpedia.backend.domain.dto.reto.*;
import com.xpedia.backend.domain.mapper.reto.RetoMapper;
import com.xpedia.backend.domain.service.reto.RetoService;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class ListarRetosUseCase {
    private final RetoService service;
    private final RetoMapper mapper;
    public ListarRetosResponse execute(ListarRetosRequest request) {
        return mapper.toListResponse(service.listar(request.rutaId(), request.nodoId()));
    }
}
