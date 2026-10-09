package com.xpedia.backend.infrastructure.presentation.dto.intento;

import com.xpedia.backend.domain.model.enums.Confianza;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record RespuestaRequest(
        @NotNull UUID preguntaId,
        @NotNull @PositiveOrZero Short elegida,
        @NotNull Confianza confianza,
        @PositiveOrZero Integer milisegundos
) {
}
