package com.xpedia.backend.domain.dto.intento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RegistrarIntentoResponse(
        UUID id,
        UUID actividadId,
        BigDecimal puntaje,
        boolean aprobado,
        int correctas,
        int total,
        OffsetDateTime terminadoEn,
        List<CorreccionItem> correcciones
) {
}
