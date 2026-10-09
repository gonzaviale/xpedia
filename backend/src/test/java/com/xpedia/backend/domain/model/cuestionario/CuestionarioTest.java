package com.xpedia.backend.domain.model.cuestionario;

import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.model.intento.Respuesta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CuestionarioTest {

    private static final UUID PREGUNTA_UNO_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_DOS_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_AJENA_ID = UUID.randomUUID();
    private static final List<String> OPCIONES = List.of("Primera", "Segunda");

    @Test
    @DisplayName("Acepta las respuestas cuando cada pregunta se responde una vez con una opción válida")
    void validarRespuestasShouldNotThrowWhenEveryPreguntaIsAnsweredOnce() {
        Cuestionario cuestionario = cuestionario();

        assertThatCode(() -> cuestionario.validarRespuestas(List.of(respuesta(PREGUNTA_UNO_ID, 0),
                respuesta(PREGUNTA_DOS_ID, 1)))).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Lanza BusinessRuleException cuando la pregunta no pertenece al cuestionario")
    void validarRespuestasShouldThrowWhenPreguntaIsNotInCuestionario() {
        Cuestionario cuestionario = cuestionario();

        assertThatThrownBy(() -> cuestionario.validarRespuestas(List.of(respuesta(PREGUNTA_AJENA_ID, 0))))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining(PREGUNTA_AJENA_ID.toString());
    }

    @Test
    @DisplayName("Lanza BusinessRuleException cuando una pregunta se responde más de una vez")
    void validarRespuestasShouldThrowWhenPreguntaIsAnsweredTwice() {
        Cuestionario cuestionario = cuestionario();

        assertThatThrownBy(() -> cuestionario.validarRespuestas(List.of(respuesta(PREGUNTA_UNO_ID, 0),
                respuesta(PREGUNTA_UNO_ID, 1)))).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("más de una vez");
    }

    @Test
    @DisplayName("Lanza BusinessRuleException cuando la opción elegida no existe")
    void validarRespuestasShouldThrowWhenOpcionDoesNotExist() {
        Cuestionario cuestionario = cuestionario();

        assertThatThrownBy(() -> cuestionario.validarRespuestas(List.of(respuesta(PREGUNTA_UNO_ID, 2),
                respuesta(PREGUNTA_DOS_ID, 0)))).isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    @DisplayName("Lanza BusinessRuleException cuando falta responder alguna pregunta")
    void validarRespuestasShouldThrowWhenAPreguntaIsMissing() {
        Cuestionario cuestionario = cuestionario();

        assertThatThrownBy(() -> cuestionario.validarRespuestas(List.of(respuesta(PREGUNTA_UNO_ID, 0))))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("2 preguntas");
    }

    @Test
    @DisplayName("Encuentra la pregunta por su id")
    void buscarPreguntaShouldReturnPreguntaWhenItBelongsToCuestionario() {
        Cuestionario cuestionario = cuestionario();

        Pregunta pregunta = cuestionario.buscarPregunta(PREGUNTA_DOS_ID);

        assertThat(pregunta.getId()).isEqualTo(PREGUNTA_DOS_ID);
    }

    // --- helpers ---
    private Cuestionario cuestionario() {
        return Cuestionario.builder()
                .id(UUID.randomUUID())
                .preguntas(List.of(pregunta(PREGUNTA_UNO_ID), pregunta(PREGUNTA_DOS_ID)))
                .build();
    }

    private Pregunta pregunta(UUID id) {
        return Pregunta.builder()
                .id(id)
                .opciones(OPCIONES)
                .correcta((short) 0)
                .build();
    }

    private Respuesta respuesta(UUID preguntaId, int elegida) {
        return Respuesta.builder()
                .preguntaId(preguntaId)
                .elegida((short) elegida)
                .build();
    }
}
