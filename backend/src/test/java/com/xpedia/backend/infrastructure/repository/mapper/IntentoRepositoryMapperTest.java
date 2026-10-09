package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.enums.Confianza;
import com.xpedia.backend.domain.model.enums.ModoIntento;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import com.xpedia.backend.infrastructure.repository.entity.IntentoEntity;
import com.xpedia.backend.infrastructure.repository.entity.RespuestaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IntentoRepositoryMapperTest {

    private static final UUID INTENTO_ID = UUID.randomUUID();
    private static final UUID USUARIO_ID = UUID.randomUUID();
    private static final UUID INSCRIPCION_ID = UUID.randomUUID();
    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final BigDecimal PUNTAJE = new BigDecimal("80.00");
    private static final OffsetDateTime INICIADO_EN = OffsetDateTime.parse("2026-10-09T14:55:00Z");
    private static final OffsetDateTime TERMINADO_EN = OffsetDateTime.parse("2026-10-09T15:00:00Z");
    private static final Short ELEGIDA = 1;
    private static final Integer MILISEGUNDOS = 2300;

    private IntentoRepositoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IntentoRepositoryMapper();
    }

    @Test
    @DisplayName("Mapea todos los campos del intento a la entidad")
    void toEntityShouldMapAllFieldsOfIntento() {
        IntentoEntity entity = mapper.toEntity(intento());

        assertThat(entity.getId()).isEqualTo(INTENTO_ID);
        assertThat(entity.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(entity.getInscripcionId()).isEqualTo(INSCRIPCION_ID);
        assertThat(entity.getActividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(entity.getModo()).isEqualTo("PRACTICA");
        assertThat(entity.getPuntaje()).isEqualTo(PUNTAJE);
        assertThat(entity.getAprobado()).isTrue();
        assertThat(entity.getIniciadoEn()).isEqualTo(INICIADO_EN);
        assertThat(entity.getTerminadoEn()).isEqualTo(TERMINADO_EN);
    }

    @Test
    @DisplayName("Mapea todos los campos de la respuesta a la entidad con el id del intento")
    void toEntityShouldMapAllFieldsOfRespuestaWithIntentoId() {
        RespuestaEntity entity = mapper.toEntity(respuesta(Confianza.DUDE), INTENTO_ID);

        assertThat(entity.getIntentoId()).isEqualTo(INTENTO_ID);
        assertThat(entity.getPreguntaId()).isEqualTo(PREGUNTA_ID);
        assertThat(entity.getElegida()).isEqualTo(ELEGIDA);
        assertThat(entity.getCorrecta()).isTrue();
        assertThat(entity.getConfianza()).isEqualTo("DUDE");
        assertThat(entity.getMilisegundos()).isEqualTo(MILISEGUNDOS);
    }

    @Test
    @DisplayName("Deja la confianza nula cuando la respuesta no la informa")
    void toEntityShouldKeepConfianzaNullWhenRespuestaHasNone() {
        RespuestaEntity entity = mapper.toEntity(respuesta(null), INTENTO_ID);

        assertThat(entity.getConfianza()).isNull();
    }

    @Test
    @DisplayName("Mapea la entidad guardada al intento de dominio con las respuestas indicadas")
    void toDomainShouldMapAllFieldsAndKeepGivenRespuestas() {
        List<Respuesta> respuestas = List.of(respuesta(Confianza.SABIA));

        Intento intento = mapper.toDomain(entity(), respuestas);

        assertThat(intento.getId()).isEqualTo(INTENTO_ID);
        assertThat(intento.getUsuarioId()).isEqualTo(USUARIO_ID);
        assertThat(intento.getInscripcionId()).isEqualTo(INSCRIPCION_ID);
        assertThat(intento.getActividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(intento.getModo()).isEqualTo(ModoIntento.VALIDACION);
        assertThat(intento.getPuntaje()).isEqualTo(PUNTAJE);
        assertThat(intento.getAprobado()).isTrue();
        assertThat(intento.getIniciadoEn()).isEqualTo(INICIADO_EN);
        assertThat(intento.getTerminadoEn()).isEqualTo(TERMINADO_EN);
        assertThat(intento.getRespuestas()).isSameAs(respuestas);
    }

    // --- helpers ---
    private Intento intento() {
        return Intento.builder()
                .id(INTENTO_ID)
                .usuarioId(USUARIO_ID)
                .inscripcionId(INSCRIPCION_ID)
                .actividadId(ACTIVIDAD_ID)
                .modo(ModoIntento.PRACTICA)
                .puntaje(PUNTAJE)
                .aprobado(true)
                .iniciadoEn(INICIADO_EN)
                .terminadoEn(TERMINADO_EN)
                .build();
    }

    private Respuesta respuesta(Confianza confianza) {
        return Respuesta.builder()
                .preguntaId(PREGUNTA_ID)
                .elegida(ELEGIDA)
                .correcta(true)
                .confianza(confianza)
                .milisegundos(MILISEGUNDOS)
                .build();
    }

    private IntentoEntity entity() {
        return IntentoEntity.builder()
                .id(INTENTO_ID)
                .usuarioId(USUARIO_ID)
                .inscripcionId(INSCRIPCION_ID)
                .actividadId(ACTIVIDAD_ID)
                .modo("VALIDACION")
                .puntaje(PUNTAJE)
                .aprobado(true)
                .iniciadoEn(INICIADO_EN)
                .terminadoEn(TERMINADO_EN)
                .build();
    }
}
