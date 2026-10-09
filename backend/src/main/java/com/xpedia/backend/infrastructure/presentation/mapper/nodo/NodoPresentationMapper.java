package com.xpedia.backend.infrastructure.presentation.mapper.nodo;

import com.xpedia.backend.domain.dto.nodo.NodoItem;
import com.xpedia.backend.domain.dto.nodo.ListarNodosRequest;
import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.infrastructure.presentation.dto.nodo.NodoResponse;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
public class NodoPresentationMapper {
    public ListarNodosRequest toRequest(UUID rutaId, UUID hitoId) {
        return new ListarNodosRequest(rutaId, hitoId);
    }

    public List<NodoResponse> toResponse(ListarNodosResponse response) {
        return response.content().stream().map(this::toResponse).toList();
    }

    private NodoResponse toResponse(NodoItem item) {
        return new NodoResponse(
                item.id(),
                item.rutaId(),
                item.hitoId(),
                item.ramaId(),
                item.habilidadId(),
                item.codigo(),
                item.titulo(),
                item.resumen(),
                item.tipo(),
                item.nivel(),
                item.minutosEstimados(),
                item.palabrasClave(),
                item.posicion(),
                item.prerrequisitoIds());
    }
}
