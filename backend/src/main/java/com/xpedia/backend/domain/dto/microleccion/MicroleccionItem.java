package com.xpedia.backend.domain.dto.microleccion;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record MicroleccionItem(
        UUID id,
        UUID rutaId,
        UUID nodoId,
        String titulo,
        Short nivel,
        Map<String, Object> contenido,
        String origen,
        OffsetDateTime revisadoEn,
        List<FuenteMicroleccionItem> fuentes
) {
}
