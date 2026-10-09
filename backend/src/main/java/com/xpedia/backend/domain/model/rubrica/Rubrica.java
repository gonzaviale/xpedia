package com.xpedia.backend.domain.model.rubrica;

import java.math.BigDecimal;
import java.util.*;

public record Rubrica(UUID id, String nombre, String descripcion, BigDecimal puntajeAprobacion,
                      List<CriterioRubrica> criterios) {
    public Rubrica { criterios = List.copyOf(criterios); }

    public BigDecimal puntajeMaximo() {
        return criterios.stream().map(c -> c.peso().multiply(BigDecimal.valueOf(c.puntajeMax())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
