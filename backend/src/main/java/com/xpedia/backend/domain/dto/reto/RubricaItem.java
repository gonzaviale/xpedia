package com.xpedia.backend.domain.dto.reto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RubricaItem(
        UUID id,
        String nombre,
        String descripcion,
        BigDecimal puntajeAprobacion,
        BigDecimal puntajeMaximo,
        List<CriterioRubricaItem> criterios
) {
}
