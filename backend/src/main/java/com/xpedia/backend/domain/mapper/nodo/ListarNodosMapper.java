package com.xpedia.backend.domain.mapper.nodo;

import com.xpedia.backend.domain.dto.nodo.NodoItem;
import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.model.nodo.Nodo;
import java.util.List;

public class ListarNodosMapper {
    public ListarNodosResponse toResponse(List<Nodo> items) {
        return new ListarNodosResponse(items.stream().map(this::toItem).toList());
    }

    private NodoItem toItem(Nodo item) {
        return new NodoItem(
                item.getId(),
                item.getRutaId(),
                item.getHitoId(),
                item.getRamaId(),
                item.getHabilidadId(),
                item.getCodigo(),
                item.getTitulo(),
                item.getResumen(),
                item.getTipo(),
                item.getNivel(),
                item.getMinutosEstimados(),
                item.getPalabrasClave(),
                item.getPosicion(),
                item.getPrerrequisitoIds());
    }
}
