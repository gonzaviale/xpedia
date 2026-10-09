package com.xpedia.backend.domain.useCase.nodo;

import com.xpedia.backend.domain.dto.nodo.ListarNodosRequest;
import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.mapper.nodo.ListarNodosMapper;
import com.xpedia.backend.domain.service.nodo.NodoService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarNodosUseCase {
    private final NodoService service;
    private final ListarNodosMapper mapper;

    public ListarNodosResponse execute(ListarNodosRequest request) {
        return mapper.toResponse(service.listar(request.rutaId(), request.hitoId()));
    }
}
