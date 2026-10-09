package com.xpedia.backend.infrastructure.presentation.dto.intento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record IntentoRequest(
        @NotNull UUID usuarioId,
        UUID inscripcionId,
        @NotNull UUID actividadId,
        OffsetDateTime iniciadoEn,
        @NotEmpty List<@Valid @NotNull RespuestaRequest> respuestas
) {
}
