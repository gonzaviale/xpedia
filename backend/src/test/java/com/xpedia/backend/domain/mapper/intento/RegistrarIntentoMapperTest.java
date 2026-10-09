package com.xpedia.backend.domain.mapper.intento;

import com.xpedia.backend.domain.dto.intento.CorreccionItem;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoResponse;
import com.xpedia.backend.domain.dto.intento.RespuestaEnviadaItem;
import com.xpedia.backend.domain.model.enums.Confianza;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarIntentoMapperTest {

    private static final UUID INTENTO_ID = UUID.randomUUID();
    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final Short ELEGIDA = 1;
    private static final Short OPCION_CORRECTA = 0;
    private static final Integer MILISEGUNDOS = 3500;
    private static final String EXPLICACION = "Porque sí";
    private static final BigDecimal PUNTAJE = new BigDecimal("50.00");
    private static final OffsetDateTime TERMINADO_EN = OffsetDateTime.parse("2026-10-09T15:00:00Z");

    private RegistrarIntentoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RegistrarIntentoMapper();
    }

    @Test
    @DisplayName("Mapea cada respuesta enviada a una respuesta sin corregir")
    void toRespuestasShouldMapSubmittedFieldsAndLeaveThemUncorrected() {
        RespuestaEnviadaItem item = new RespuestaEnviadaItem(PREGUNTA_ID, ELEGIDA, Confianza.ADIVINE, MILISEGUNDOS);

        List<Respuesta> respuestas = mapper.toRespuestas(List.of(item));

        assertThat(respuestas).hasSize(1);
        Respuesta respuesta = respuestas.getFirst();
        assertThat(respuesta.getPreguntaId()).isEqualTo(PREGUNTA_ID);
        assertThat(respuesta.getElegida()).isEqualTo(ELEGIDA);
        assertThat(respuesta.getConfianza()).isEqualTo(Confianza.ADIVINE);
        assertThat(respuesta.getMilisegundos()).isEqualTo(MILISEGUNDOS);
        assertThat(respuesta.getCorrecta()).isNull();
    }

    @Test
    @DisplayName("Mapea todos los campos del intento y de su corrección")
    void toResponseShouldMapAllFieldsOfIntentoAndCorrection() {
        RegistrarIntentoResponse response = mapper.toResponse(intento());

        thenResponseHasAllFields(response);
    }

    @Test
    @DisplayName("Cuenta las respuestas correctas y el total")
    void toResponseShouldCountCorrectasAndTotal() {
        RegistrarIntentoResponse response = mapper.toResponse(intento());

        assertThat(response.correctas()).isZero();
        assertThat(response.total()).isEqualTo(1);
    }

    // --- helpers ---
    private Intento intento() {
        Respuesta respuesta = Respuesta.builder()
                .preguntaId(PREGUNTA_ID)
                .elegida(ELEGIDA)
                .correcta(false)
                .confianza(Confianza.SABIA)
                .milisegundos(MILISEGUNDOS)
                .opcionCorrecta(OPCION_CORRECTA)
                .explicacion(EXPLICACION)
                .build();
        return Intento.builder()
                .id(INTENTO_ID)
                .actividadId(ACTIVIDAD_ID)
                .puntaje(PUNTAJE)
                .aprobado(false)
                .terminadoEn(TERMINADO_EN)
                .respuestas(List.of(respuesta))
                .build();
    }

    // --- assert ---
    private void thenResponseHasAllFields(RegistrarIntentoResponse response) {
        assertThat(response.id()).isEqualTo(INTENTO_ID);
        assertThat(response.actividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(response.puntaje()).isEqualTo(PUNTAJE);
        assertThat(response.aprobado()).isFalse();
        assertThat(response.terminadoEn()).isEqualTo(TERMINADO_EN);
        CorreccionItem correccion = response.correcciones().getFirst();
        assertThat(correccion.preguntaId()).isEqualTo(PREGUNTA_ID);
        assertThat(correccion.elegida()).isEqualTo(ELEGIDA);
        assertThat(correccion.correcta()).isFalse();
        assertThat(correccion.opcionCorrecta()).isEqualTo(OPCION_CORRECTA);
        assertThat(correccion.confianza()).isEqualTo(Confianza.SABIA);
        assertThat(correccion.explicacion()).isEqualTo(EXPLICACION);
    }
}
