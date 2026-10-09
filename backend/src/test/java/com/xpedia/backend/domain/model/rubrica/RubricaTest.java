package com.xpedia.backend.domain.model.rubrica;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.xpedia.backend.support.RetoTestData.rubrica;

class RubricaTest {
    @Test void maximoUsaPesosDecimalesSinPerderPrecision() {
        assertThat(rubrica().puntajeMaximo()).isEqualByComparingTo("2.25");
    }
    @Test void rubricaVaciaTieneMaximoCero() {
        assertThat(new Rubrica(UUID.randomUUID(), "Vacía", null, BigDecimal.ZERO, List.of()).puntajeMaximo()).isEqualByComparingTo("0");
    }
    @Test void conservaOrdenYCriteriosSinPermitirCambiarLaColeccionOriginal() {
        var original = new ArrayList<>(rubrica().criterios());
        var result = new Rubrica(UUID.randomUUID(), "Prueba", null, BigDecimal.ONE, original);
        original.clear(); assertThat(result.criterios()).hasSize(2);
        assertThatThrownBy(() -> result.criterios().clear()).isInstanceOf(UnsupportedOperationException.class);
        assertThat(result.criterios().get(1).eliminatorio()).isTrue();
    }
}
