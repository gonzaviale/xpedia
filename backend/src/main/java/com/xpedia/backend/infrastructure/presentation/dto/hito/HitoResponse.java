package com.xpedia.backend.infrastructure.presentation.dto.hito;

import java.math.BigDecimal;
import java.util.UUID;

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
