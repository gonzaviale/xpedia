package com.xpedia.backend.domain.dto.cuestionario;

import com.xpedia.backend.domain.model.enums.TipoPregunta;

import java.util.List;
import java.util.UUID;

public record PreguntaItem(
        UUID id,
        Short posicion,
        TipoPregunta tipo,
        String enunciado,
        List<String> opciones
) {
}
