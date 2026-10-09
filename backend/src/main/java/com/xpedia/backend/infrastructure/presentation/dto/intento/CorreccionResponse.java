package com.xpedia.backend.infrastructure.presentation.dto.intento;

import com.xpedia.backend.domain.model.enums.Confianza;

import java.util.UUID;

public record CorreccionResponse(
        UUID preguntaId,
        Short elegida,
        boolean correcta,
        Short opcionCorrecta,
        Confianza confianza,
        String explicacion
) {
}
