package com.xpedia.backend.domain.model.cuestionario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PreguntaTest {

    private static final List<String> OPCIONES = List.of("Primera", "Segunda", "Tercera");
    private static final Short CORRECTA = 1;

    @ParameterizedTest
    @ValueSource(shorts = {0, 1, 2})
    @DisplayName("Admite una opción que está dentro del rango de opciones")
    void admiteOpcionShouldReturnTrueWhenOpcionIsInRange(short elegida) {
        boolean admite = pregunta().admiteOpcion(elegida);

        assertThat(admite).isTrue();
    }

    @ParameterizedTest
    @ValueSource(shorts = {-1, 3, 10})
    @DisplayName("No admite una opción fuera del rango de opciones")
    void admiteOpcionShouldReturnFalseWhenOpcionIsOutOfRange(short elegida) {
        boolean admite = pregunta().admiteOpcion(elegida);

        assertThat(admite).isFalse();
    }

    @Test
    @DisplayName("No admite una opción nula")
    void admiteOpcionShouldReturnFalseWhenOpcionIsNull() {
        boolean admite = pregunta().admiteOpcion(null);

        assertThat(admite).isFalse();
    }

    @Test
    @DisplayName("Es correcta cuando la opción elegida coincide con la correcta")
    void esCorrectaShouldReturnTrueWhenElegidaMatches() {
        boolean correcta = pregunta().esCorrecta(CORRECTA);

        assertThat(correcta).isTrue();
    }

    @Test
    @DisplayName("No es correcta cuando la opción elegida es otra")
    void esCorrectaShouldReturnFalseWhenElegidaDiffers() {
        boolean correcta = pregunta().esCorrecta((short) 2);

        assertThat(correcta).isFalse();
    }

    // --- helpers ---
    private Pregunta pregunta() {
        return Pregunta.builder()
                .opciones(OPCIONES)
                .correcta(CORRECTA)
                .build();
    }
}
