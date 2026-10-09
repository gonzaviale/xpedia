package com.xpedia.backend.support;

import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.model.rubrica.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

public final class RetoTestData {
    private RetoTestData() {}
    public static Rubrica rubrica() {
        return new Rubrica(UUID.randomUUID(), "Escritura", null, new BigDecimal("2.25"),
                List.of(new CriterioRubrica(UUID.randomUUID(), (short)1, "Claridad", "Detalle", (short)3, new BigDecimal("0.50"), false),
                        new CriterioRubrica(UUID.randomUUID(), (short)2, "Política", null, (short)1, new BigDecimal("0.75"), true)));
    }
    public static Reto reto() {
        return Reto.builder().id(UUID.randomUUID()).rutaId(UUID.randomUUID()).nodoId(UUID.randomUUID())
                .hitoId(UUID.randomUUID()).tipo(TipoReto.ENSAYO).titulo("Consigna de prueba").nivel((short)2)
                .contenido(Map.of("consigna", "Responder ñ", "contexto", "Caso ficticio", "formatoEntrega", "Texto"))
                .origen("IA").revisadoEn(OffsetDateTime.parse("2026-10-08T10:00:00-03:00")).rubrica(rubrica()).build();
    }
}
