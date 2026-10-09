package com.xpedia.backend.domain.model.intento;

import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.model.cuestionario.Pregunta;
import com.xpedia.backend.domain.model.enums.ModoIntento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class IntentoTest {

    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID USUARIO_ID = UUID.randomUUID();
    private static final UUID INSCRIPCION_ID = UUID.randomUUID();
    private static final OffsetDateTime INICIADO_EN = OffsetDateTime.parse("2026-10-09T14:55:00Z");
    private static final OffsetDateTime TERMINADO_EN = OffsetDateTime.parse("2026-10-09T15:00:00Z");

    @Test
    @DisplayName("Aprueba con exactamente 80 % de aciertos")
    void corregirShouldApproveWhenPuntajeIsExactlyEighty() {
        Cuestionario cuestionario = cuestionario(5);

        Intento intento = corregir(cuestionario, respuestasConAciertos(cuestionario, 4), INICIADO_EN);

        assertThat(intento.getPuntaje()).isEqualByComparingTo("80.00");
        assertThat(intento.getAprobado()).isTrue();
    }

    @Test
    @DisplayName("Desaprueba con menos de 80 % de aciertos")
    void corregirShouldNotApproveWhenPuntajeIsBelowEighty() {
        Cuestionario cuestionario = cuestionario(5);

        Intento intento = corregir(cuestionario, respuestasConAciertos(cuestionario, 3), INICIADO_EN);

        assertThat(intento.getPuntaje()).isEqualByComparingTo("60.00");
        assertThat(intento.getAprobado()).isFalse();
    }

    @Test
    @DisplayName("Redondea el puntaje a dos decimales")
    void corregirShouldRoundPuntajeToTwoDecimals() {
        Cuestionario cuestionario = cuestionario(3);

        Intento intento = corregir(cuestionario, respuestasConAciertos(cuestionario, 1), INICIADO_EN);

        assertThat(intento.getPuntaje()).isEqualTo(new BigDecimal("33.33"));
    }

    @Test
    @DisplayName("Copia el usuario, la inscripción, la actividad y el modo práctica")
    void corregirShouldCopyIdentifiersAndUsePracticaMode() {
        Cuestionario cuestionario = cuestionario(2);

        Intento intento = corregir(cuestionario, respuestasConAciertos(cuestionario, 2), INICIADO_EN);

        assertThat(intento.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(intento.getInscripcionId()).isEqualTo(INSCRIPCION_ID);
        assertThat(intento.getActividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(intento.getModo()).isEqualTo(ModoIntento.PRACTICA);
        assertThat(intento.getIniciadoEn()).isEqualTo(INICIADO_EN);
        assertThat(intento.getTerminadoEn()).isEqualTo(TERMINADO_EN);
    }

    @Test
    @DisplayName("Usa la fecha de fin como inicio cuando no se informa el inicio")
    void corregirShouldUseTerminadoEnAsIniciadoEnWhenItIsNull() {
        Cuestionario cuestionario = cuestionario(2);

        Intento intento = corregir(cuestionario, respuestasConAciertos(cuestionario, 2), null);

        assertThat(intento.getIniciadoEn()).isEqualTo(TERMINADO_EN);
    }

    @Test
    @DisplayName("Ordena las respuestas corregidas por la posición de la pregunta")
    void corregirShouldOrderRespuestasByPreguntaPosition() {
        Cuestionario cuestionario = cuestionario(3);
        List<Respuesta> enviadas = new ArrayList<>(respuestasConAciertos(cuestionario, 3));
        Collections.reverse(enviadas);

        Intento intento = corregir(cuestionario, enviadas, INICIADO_EN);

        assertThat(intento.getRespuestas())
                .extracting(Respuesta::getPreguntaId)
                .containsExactlyElementsOf(cuestionario.getPreguntas().stream().map(Pregunta::getId).toList());
    }

    @Test
    @DisplayName("Cuenta las respuestas correctas y el total")
    void cantidadCorrectasAndTotalShouldCountRespuestas() {
        Cuestionario cuestionario = cuestionario(4);

        Intento intento = corregir(cuestionario, respuestasConAciertos(cuestionario, 3), INICIADO_EN);

        assertThat(intento.cantidadCorrectas()).isEqualTo(3);
        assertThat(intento.total()).isEqualTo(4);
    }

    // --- act ---
    private Intento corregir(Cuestionario cuestionario, List<Respuesta> respuestas, OffsetDateTime iniciadoEn) {
        return Intento.corregir(cuestionario, USUARIO_ID, INSCRIPCION_ID, respuestas, iniciadoEn, TERMINADO_EN);
    }

    // --- helpers ---
    private Cuestionario cuestionario(int cantidadPreguntas) {
        List<Pregunta> preguntas = IntStream.range(0, cantidadPreguntas)
                .mapToObj(this::pregunta)
                .toList();
        return Cuestionario.builder()
                .id(ACTIVIDAD_ID)
                .preguntas(preguntas)
                .build();
    }

    private Pregunta pregunta(int posicion) {
        return Pregunta.builder()
                .id(UUID.randomUUID())
                .posicion((short) posicion)
                .opciones(List.of("Correcta", "Incorrecta"))
                .correcta((short) 0)
                .build();
    }

    private List<Respuesta> respuestasConAciertos(Cuestionario cuestionario, int aciertos) {
        List<Pregunta> preguntas = cuestionario.getPreguntas();
        return IntStream.range(0, preguntas.size())
                .mapToObj(indice -> Respuesta.builder()
                        .preguntaId(preguntas.get(indice).getId())
                        .elegida((short) (indice < aciertos ? 0 : 1))
                        .build())
                .toList();
    }
}
