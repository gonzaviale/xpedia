package com.xpedia.backend.domain.dto.puesto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ActualizarPuestoResponse(UUID id, UUID organizacionId, String nombre, OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) {
}
