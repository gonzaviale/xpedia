package com.xpedia.backend.domain.dto.hito;

import java.util.UUID;
import java.math.BigDecimal;

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
