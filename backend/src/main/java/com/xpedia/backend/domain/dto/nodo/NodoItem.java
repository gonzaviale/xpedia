package com.xpedia.backend.domain.dto.nodo;

import java.util.UUID;
import java.util.List;
import com.xpedia.backend.domain.model.enums.TipoNodo;

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
