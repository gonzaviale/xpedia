package com.xpedia.backend.domain.dto.hito;

import java.math.BigDecimal;
import java.util.UUID;

public record HitoItem(
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
