package com.xpedia.backend.domain.mapper.nodo;

import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.dto.nodo.NodoItem;
import com.xpedia.backend.domain.model.nodo.Nodo;

import java.util.List;

public class ListarNodosMapper {

    public ListarNodosResponse toResponse(List<Nodo> nodos) {
        return new ListarNodosResponse(nodos.stream().map(this::toItem).toList());
    }

    private NodoItem toItem(Nodo nodo) {
        return new NodoItem(
                nodo.getId(),
                nodo.getRutaId(),
                nodo.getHitoId(),
                nodo.getRamaId(),
                nodo.getHabilidadId(),
                nodo.getCodigo(),
                nodo.getTitulo(),
                nodo.getResumen(),
                nodo.getTipo(),
                nodo.getNivel(),
                nodo.getMinutosEstimados(),
                nodo.getPalabrasClave(),
                nodo.getPosicion(),
                nodo.getPrerrequisitoIds());
    }
}
