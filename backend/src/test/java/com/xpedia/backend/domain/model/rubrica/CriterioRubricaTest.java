package com.xpedia.backend.domain.model.rubrica;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CriterioRubricaTest {

    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");

    @Test
    @DisplayName("Multiplica el peso por el puntaje máximo")
    void puntajeMaximoPonderadoShouldMultiplyPesoByPuntajeMax() {
        CriterioRubrica criterio = criterio((short) 3, new BigDecimal("0.50"));

        BigDecimal ponderado = criterio.puntajeMaximoPonderado();

        assertThat(ponderado).isEqualByComparingTo("1.50");
    }

    @Test
    @DisplayName("Conserva la precisión decimal del peso")
    void puntajeMaximoPonderadoShouldKeepDecimalPrecision() {
        CriterioRubrica criterio = criterio((short) 1, new BigDecimal("0.75"));

        BigDecimal ponderado = criterio.puntajeMaximoPonderado();

        assertThat(ponderado).isEqualByComparingTo("0.75");
    }

    @Test
    @DisplayName("Devuelve cero cuando el peso es cero")
    void puntajeMaximoPonderadoShouldReturnZeroWhenPesoIsZero() {
        CriterioRubrica criterio = criterio((short) 3, BigDecimal.ZERO);

        BigDecimal ponderado = criterio.puntajeMaximoPonderado();

        assertThat(ponderado).isEqualByComparingTo("0");
    }

    // --- helpers ---
    private CriterioRubrica criterio(Short puntajeMax, BigDecimal peso) {
        return new CriterioRubrica(CRITERIO_ID, (short) 1, "Claridad", null, puntajeMax, peso, false);
    }
}
