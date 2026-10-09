package com.xpedia.backend.domain.model.rubrica;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record Rubrica(
        UUID id,
        String nombre,
        String descripcion,
        BigDecimal puntajeAprobacion,
        List<CriterioRubrica> criterios
) {

    public Rubrica {
        criterios = List.copyOf(criterios);
    }

    public BigDecimal puntajeMaximo() {
        return criterios.stream()
                .map(CriterioRubrica::puntajeMaximoPonderado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
