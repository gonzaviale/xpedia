package com.xpedia.backend.infrastructure.presentation.dto.cuestionario;

import com.xpedia.backend.domain.model.enums.TipoPregunta;

import java.util.List;
import java.util.UUID;

public record PreguntaResponse(
        UUID id,
        Short posicion,
        TipoPregunta tipo,
        String enunciado,
        List<String> opciones
) {
}
