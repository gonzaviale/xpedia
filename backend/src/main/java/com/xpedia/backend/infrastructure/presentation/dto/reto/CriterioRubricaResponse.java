package com.xpedia.backend.infrastructure.presentation.dto.reto;

import java.math.BigDecimal;
import java.util.UUID;

public record CriterioRubricaResponse(
        UUID id,
        Short posicion,
        String nombre,
        String descripcion,
        Short puntajeMax,
        BigDecimal peso,
        boolean eliminatorio
) {
}
