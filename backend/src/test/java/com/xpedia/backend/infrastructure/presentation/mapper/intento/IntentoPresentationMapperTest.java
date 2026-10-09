package com.xpedia.backend.infrastructure.presentation.mapper.intento;

import com.xpedia.backend.domain.dto.intento.CorreccionItem;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoRequest;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoResponse;
import com.xpedia.backend.domain.dto.intento.RespuestaEnviadaItem;
import com.xpedia.backend.domain.model.enums.Confianza;
import com.xpedia.backend.infrastructure.presentation.dto.intento.CorreccionResponse;
import com.xpedia.backend.infrastructure.presentation.dto.intento.IntentoRequest;
import com.xpedia.backend.infrastructure.presentation.dto.intento.IntentoResponse;
import com.xpedia.backend.infrastructure.presentation.dto.intento.RespuestaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IntentoPresentationMapperTest {

    private static final UUID INTENTO_ID = UUID.randomUUID();
    private static final UUID USUARIO_ID = UUID.randomUUID();
    private static final UUID INSCRIPCION_ID = UUID.randomUUID();
    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final Short ELEGIDA = 1;
    private static final Short OPCION_CORRECTA = 0;
    private static final Integer MILISEGUNDOS = 2100;
    private static final String EXPLICACION = "Porque sí";
    private static final BigDecimal PUNTAJE = new BigDecimal("50.00");
    private static final OffsetDateTime INICIADO_EN = OffsetDateTime.parse("2026-10-09T14:55:00Z");
    private static final OffsetDateTime TERMINADO_EN = OffsetDateTime.parse("2026-10-09T15:00:00Z");

    private IntentoPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IntentoPresentationMapper();
    }

    @Test
    @DisplayName("Mapea el pedido HTTP y sus respuestas al pedido de dominio")
    void toRequestShouldMapAllFieldsOfRequestAndRespuestas() {
        RespuestaRequest respuesta = new RespuestaRequest(PREGUNTA_ID, ELEGIDA, Confianza.DUDE, MILISEGUNDOS);
        IntentoRequest request = new IntentoRequest(
                USUARIO_ID,
                INSCRIPCION_ID,
                ACTIVIDAD_ID,
                INICIADO_EN,
                List.of(respuesta));

        RegistrarIntentoRequest result = mapper.toRequest(request);

        assertThat(result.usuarioId()).isEqualTo(USUARIO_ID);
        assertThat(result.inscripcionId()).isEqualTo(INSCRIPCION_ID);
        assertThat(result.actividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(result.iniciadoEn()).isEqualTo(INICIADO_EN);
        RespuestaEnviadaItem item = result.respuestas().getFirst();
        assertThat(item.preguntaId()).isEqualTo(PREGUNTA_ID);
        assertThat(item.elegida()).isEqualTo(ELEGIDA);
        assertThat(item.confianza()).isEqualTo(Confianza.DUDE);
        assertThat(item.milisegundos()).isEqualTo(MILISEGUNDOS);
    }

    @Test
    @DisplayName("Conserva los datos opcionales nulos del pedido")
    void toRequestShouldKeepOptionalFieldsNull() {
        RespuestaRequest respuesta = new RespuestaRequest(PREGUNTA_ID, ELEGIDA, Confianza.SABIA, null);
        IntentoRequest request = new IntentoRequest(USUARIO_ID, null, ACTIVIDAD_ID, null, List.of(respuesta));

        RegistrarIntentoRequest result = mapper.toRequest(request);

        assertThat(result.inscripcionId()).isNull();
        assertThat(result.iniciadoEn()).isNull();
        assertThat(result.respuestas().getFirst().milisegundos()).isNull();
    }

    @Test
    @DisplayName("Mapea todos los campos del resultado y de cada corrección a la respuesta HTTP")
    void toResponseShouldMapAllFieldsOfIntentoAndCorrecciones() {
        CorreccionItem correccion = new CorreccionItem(
                PREGUNTA_ID,
                ELEGIDA,
                false,
                OPCION_CORRECTA,
                Confianza.ADIVINE,
                EXPLICACION);
        RegistrarIntentoResponse response = new RegistrarIntentoResponse(
                INTENTO_ID,
                ACTIVIDAD_ID,
                PUNTAJE,
                false,
                1,
                2,
                TERMINADO_EN,
                List.of(correccion));

        IntentoResponse result = mapper.toResponse(response);

        assertThat(result.id()).isEqualTo(INTENTO_ID);
        assertThat(result.actividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(result.puntaje()).isEqualTo(PUNTAJE);
        assertThat(result.aprobado()).isFalse();
        assertThat(result.correctas()).isEqualTo(1);
        assertThat(result.total()).isEqualTo(2);
        assertThat(result.terminadoEn()).isEqualTo(TERMINADO_EN);
        CorreccionResponse mapeada = result.correcciones().getFirst();
        assertThat(mapeada.preguntaId()).isEqualTo(PREGUNTA_ID);
        assertThat(mapeada.elegida()).isEqualTo(ELEGIDA);
        assertThat(mapeada.correcta()).isFalse();
        assertThat(mapeada.opcionCorrecta()).isEqualTo(OPCION_CORRECTA);
        assertThat(mapeada.confianza()).isEqualTo(Confianza.ADIVINE);
        assertThat(mapeada.explicacion()).isEqualTo(EXPLICACION);
    }
}
