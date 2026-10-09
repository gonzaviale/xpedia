package com.xpedia.backend.domain.dto.cuestionario;

import java.util.List;
import java.util.UUID;

public record CuestionarioItem(
        UUID id,
        UUID rutaId,
        UUID nodoId,
        String titulo,
        Short nivel,
        List<PreguntaItem> preguntas
) {
}
