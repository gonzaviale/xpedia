package com.xpedia.backend.infrastructure.presentation.dto.hito;

import java.util.UUID;
import java.math.BigDecimal;

public record HitoResponse(
        UUID id,
        UUID rutaId,
        Short posicion,
        String titulo,
        String objetivo,
        BigDecimal horasEstimadas,
        Boolean esFinal,
        String evidenciaEsperada
) {
}
