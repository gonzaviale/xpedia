package com.xpedia.backend.domain.dto.reto;

import java.math.BigDecimal;
import java.util.UUID;

public record CriterioRubricaItem(
        UUID id,
        Short posicion,
        String nombre,
        String descripcion,
        Short puntajeMax,
        BigDecimal peso,
        boolean eliminatorio
) {
}
