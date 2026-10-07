package com.xpedia.backend.infrastructure.presentation.dto.puesto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PuestoResponse(
        UUID id,
        UUID organizacionId,
        String nombre,
        OffsetDateTime creadoEn,
        OffsetDateTime actualizadoEn
) {
}
