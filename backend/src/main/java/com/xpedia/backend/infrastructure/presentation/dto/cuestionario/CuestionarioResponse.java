package com.xpedia.backend.infrastructure.presentation.dto.cuestionario;

import java.util.List;
import java.util.UUID;

public record CuestionarioResponse(
        UUID id,
        UUID rutaId,
        UUID nodoId,
        String titulo,
        Short nivel,
        List<PreguntaResponse> preguntas
) {
}
