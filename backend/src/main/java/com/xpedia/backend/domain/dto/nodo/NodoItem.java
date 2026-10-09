package com.xpedia.backend.domain.dto.nodo;

import com.xpedia.backend.domain.model.enums.TipoNodo;

import java.util.List;
import java.util.UUID;

public record NodoItem(
        UUID id,
        UUID rutaId,
        UUID hitoId,
        UUID ramaId,
        UUID habilidadId,
        String codigo,
        String titulo,
        String resumen,
        TipoNodo tipo,
        Short nivel,
        Short minutosEstimados,
        List<String> palabrasClave,
        Short posicion,
        List<UUID> prerrequisitoIds
) {
}
