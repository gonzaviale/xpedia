package com.xpedia.backend.domain.model.rubrica;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RubricaTest {

    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");
    private static final UUID OTRO_CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000002");

    @Test
    @DisplayName("Suma los máximos ponderados de los criterios sin perder precisión decimal")
    void puntajeMaximoShouldSumWeightedCriteriaWithoutLosingPrecision() {
        Rubrica rubrica = rubricaConCriterios(List.of(claridad(), politica()));

        BigDecimal maximo = rubrica.puntajeMaximo();

        assertThat(maximo).isEqualByComparingTo("2.25");
    }

    @Test
    @DisplayName("Devuelve cero cuando la rúbrica no tiene criterios")
    void puntajeMaximoShouldReturnZeroWhenNoCriterios() {
        Rubrica rubrica = rubricaConCriterios(List.of());

        BigDecimal maximo = rubrica.puntajeMaximo();

        assertThat(maximo).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Copia los criterios recibidos y no refleja cambios posteriores en la lista original")
    void constructorShouldCopyCriteriosFromOriginalList() {
        List<CriterioRubrica> original = new ArrayList<>(List.of(claridad(), politica()));
        Rubrica rubrica = rubricaConCriterios(original);

        original.clear();

        assertThat(rubrica.criterios()).hasSize(2);
    }

    @Test
    @DisplayName("Conserva el orden de los criterios")
    void constructorShouldKeepCriteriosOrder() {
        Rubrica rubrica = rubricaConCriterios(List.of(claridad(), politica()));

        assertThat(rubrica.criterios()).extracting(CriterioRubrica::id)
                .containsExactly(CRITERIO_ID, OTRO_CRITERIO_ID);
    }

    @Test
    @DisplayName("Expone los criterios como lista inmutable")
    void criteriosShouldBeUnmodifiable() {
        Rubrica rubrica = rubricaConCriterios(List.of(claridad(), politica()));

        assertThatThrownBy(() -> rubrica.criterios().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    // --- helpers ---
    private Rubrica rubricaConCriterios(List<CriterioRubrica> criterios) {
        return new Rubrica(RUBRICA_ID, "Escritura", null, new BigDecimal("2.25"), criterios);
    }

    private CriterioRubrica claridad() {
        return new CriterioRubrica(
                CRITERIO_ID, (short) 1, "Claridad", "Detalle", (short) 3, new BigDecimal("0.50"), false);
    }

    private CriterioRubrica politica() {
        return new CriterioRubrica(
                OTRO_CRITERIO_ID, (short) 2, "Política", null, (short) 1, new BigDecimal("0.75"), true);
    }
}
