package com.xpedia.backend.domain.useCase.nodo;

import com.xpedia.backend.domain.dto.nodo.ListarNodosRequest;
import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.mapper.nodo.ListarNodosMapper;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.service.nodo.NodoService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListarNodosUseCase {

    private final NodoService nodoService;
    private final ListarNodosMapper mapper;

    public ListarNodosResponse execute(ListarNodosRequest request) {
        List<Nodo> nodos = nodoService.listar(request.rutaId(), request.hitoId());
        return mapper.toResponse(nodos);
    }
}
