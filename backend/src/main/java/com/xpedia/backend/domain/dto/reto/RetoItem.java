package com.xpedia.backend.domain.dto.reto;

import com.xpedia.backend.domain.model.enums.TipoReto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record RetoItem(
        UUID id,
        UUID rutaId,
        UUID nodoId,
        UUID hitoId,
        TipoReto tipo,
        String titulo,
        Short nivel,
        Map<String, Object> contenido,
        String origen,
        OffsetDateTime revisadoEn,
        RubricaItem rubrica
) {
}
