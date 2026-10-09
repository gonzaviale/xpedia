package com.xpedia.backend.domain.dto.intento;

import com.xpedia.backend.domain.model.enums.Confianza;

import java.util.UUID;

public record CorreccionItem(
        UUID preguntaId,
        Short elegida,
        boolean correcta,
        Short opcionCorrecta,
        Confianza confianza,
        String explicacion
) {
}
