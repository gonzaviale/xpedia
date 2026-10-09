package com.xpedia.backend.domain.model.intento;

import com.xpedia.backend.domain.model.cuestionario.Pregunta;
import com.xpedia.backend.domain.model.enums.Confianza;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RespuestaTest {

    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final Short CORRECTA = 1;
    private static final String EXPLICACION = "Porque sí";
    private static final Integer MILISEGUNDOS = 4200;

    @Test
    @DisplayName("Marca la respuesta como correcta y completa la corrección cuando acierta")
    void corregirShouldMarkCorrectaAndFillCorrectionWhenElegidaMatches() {
        Respuesta respuesta = respuesta((short) 1);

        Respuesta corregida = respuesta.corregir(pregunta());

        assertThat(corregida.getCorrecta()).isTrue();
        assertThat(corregida.getOpcionCorrecta()).isEqualTo(CORRECTA);
        assertThat(corregida.getExplicacion()).isEqualTo(EXPLICACION);
    }

    @Test
    @DisplayName("Marca la respuesta como incorrecta cuando elige otra opción")
    void corregirShouldMarkIncorrectaWhenElegidaDiffers() {
        Respuesta respuesta = respuesta((short) 0);

        Respuesta corregida = respuesta.corregir(pregunta());

        assertThat(corregida.getCorrecta()).isFalse();
    }

    @Test
    @DisplayName("Conserva los datos enviados y no modifica la respuesta original")
    void corregirShouldKeepSubmittedDataAndLeaveOriginalUntouched() {
        Respuesta respuesta = respuesta((short) 0);

        Respuesta corregida = respuesta.corregir(pregunta());

        assertThat(corregida.getPreguntaId()).isEqualTo(PREGUNTA_ID);
        assertThat(corregida.getElegida()).isEqualTo((short) 0);
        assertThat(corregida.getConfianza()).isEqualTo(Confianza.DUDE);
        assertThat(corregida.getMilisegundos()).isEqualTo(MILISEGUNDOS);
        assertThat(respuesta.getCorrecta()).isNull();
    }

    // --- helpers ---
    private Respuesta respuesta(Short elegida) {
        return Respuesta.builder()
                .preguntaId(PREGUNTA_ID)
                .elegida(elegida)
                .confianza(Confianza.DUDE)
                .milisegundos(MILISEGUNDOS)
                .build();
    }

    private Pregunta pregunta() {
        return Pregunta.builder()
                .id(PREGUNTA_ID)
                .opciones(List.of("Primera", "Segunda"))
                .correcta(CORRECTA)
                .explicacion(EXPLICACION)
                .build();
    }
}
