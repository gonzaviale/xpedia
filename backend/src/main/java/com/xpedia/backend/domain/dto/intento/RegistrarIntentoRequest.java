package com.xpedia.backend.domain.dto.intento;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RegistrarIntentoRequest(
        UUID usuarioId,
        UUID inscripcionId,
        UUID actividadId,
        OffsetDateTime iniciadoEn,
        List<RespuestaEnviadaItem> respuestas
) {
}
