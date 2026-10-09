package com.xpedia.backend.infrastructure.presentation.dto.inscripcion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RitmoMinDeserializerTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    @DisplayName("Lee minutos enteros sin cambiar su valor")
    void deserializeShouldReadIntegerMinutes() {
        CrearInscripcionWebRequest request = leer("20");

        thenDeserializeShouldReadIntegerMinutes(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {"20.5", "20.0", "\"20\"", "true", "{}", "[]", "40000"})
    @DisplayName("Rechaza decimales, conversiones implícitas y valores que exceden smallint")
    void deserializeShouldRejectNonIntegerOrOverflow(String valor) {
        thenDeserializeShouldRejectNonIntegerOrOverflow(valor);
    }

    // --- act ---
    private CrearInscripcionWebRequest leer(String valor) {
        return mapper.readValue("{\"ritmoMin\":" + valor + "}", CrearInscripcionWebRequest.class);
    }

    // --- assert ---
    private void thenDeserializeShouldReadIntegerMinutes(CrearInscripcionWebRequest request) {
        assertThat(request.ritmoMin()).isEqualTo((short) 20);
    }

    private void thenDeserializeShouldRejectNonIntegerOrOverflow(String valor) {
        assertThatThrownBy(() -> leer(valor)).isInstanceOf(JacksonException.class);
    }
}
