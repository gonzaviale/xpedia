package com.xpedia.backend.domain.model.rubrica;

import java.math.BigDecimal;
import java.util.UUID;

public record CriterioRubrica(
        UUID id,
        Short posicion,
        String nombre,
        String descripcion,
        Short puntajeMax,
        BigDecimal peso,
        boolean eliminatorio
) {

    public BigDecimal puntajeMaximoPonderado() {
        return peso.multiply(BigDecimal.valueOf(puntajeMax));
    }
}
