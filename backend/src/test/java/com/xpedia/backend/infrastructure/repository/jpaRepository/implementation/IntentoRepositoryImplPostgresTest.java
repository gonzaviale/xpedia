package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.enums.Confianza;
import com.xpedia.backend.domain.model.enums.ModoIntento;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import com.xpedia.backend.domain.repository.intento.IntentoRepository;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql({"/db/rutas-test.sql", "/db/cuestionarios-test.sql"})
class IntentoRepositoryImplPostgresTest extends PostgresRepositoryTestSupport {

    private static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000c01");
    private static final UUID ACTIVIDAD_ID = UUID.fromString("00000000-0000-0000-0000-000000000a01");
    private static final UUID PRIMERA_PREGUNTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000b01");
    private static final UUID SEGUNDA_PREGUNTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000b02");
    private static final BigDecimal PUNTAJE = new BigDecimal("50.00");
    private static final OffsetDateTime INICIADO_EN = OffsetDateTime.parse("2026-10-09T14:55:00Z");
    private static final OffsetDateTime TERMINADO_EN = OffsetDateTime.parse("2026-10-09T15:00:00Z");

    @Autowired
    private IntentoRepository intentoRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @AfterEach
    void cleanUp() {
        jdbc.update("DELETE FROM intento");
    }

    @Test
    @DisplayName("Guarda el intento con su puntaje, modo y fechas")
    void saveShouldPersistIntentoWithPuntajeModeAndDates() {
        Intento guardado = intentoRepository.save(intento(USUARIO_ID));

        Map<String, Object> fila = jdbc.queryForMap("SELECT * FROM intento WHERE id = ?", guardado.getId());
        assertThat(fila.get("usuario_id")).isEqualTo(USUARIO_ID);
        assertThat(fila.get("actividad_id")).isEqualTo(ACTIVIDAD_ID);
        assertThat(fila.get("modo")).isEqualTo("PRACTICA");
        assertThat((BigDecimal) fila.get("puntaje")).isEqualByComparingTo(PUNTAJE);
        assertThat(fila.get("aprobado")).isEqualTo(false);
        assertThat(fila.get("inscripcion_id")).isNull();
    }

    @Test
    @DisplayName("Guarda una respuesta por pregunta con su corrección y confianza")
    void saveShouldPersistOneRespuestaPerPreguntaWithConfianza() {
        Intento guardado = intentoRepository.save(intento(USUARIO_ID));

        List<Map<String, Object>> filas = jdbc.queryForList(
                "SELECT * FROM respuesta WHERE intento_id = ? ORDER BY pregunta_id", guardado.getId());
        assertThat(filas).hasSize(2);
        assertThat(filas.get(0).get("pregunta_id")).isEqualTo(PRIMERA_PREGUNTA_ID);
        assertThat(filas.get(0).get("correcta")).isEqualTo(true);
        assertThat(filas.get(0).get("confianza")).isEqualTo("SABIA");
        assertThat(filas.get(1).get("pregunta_id")).isEqualTo(SEGUNDA_PREGUNTA_ID);
        assertThat(filas.get(1).get("correcta")).isEqualTo(false);
        assertThat(filas.get(1).get("confianza")).isEqualTo("ADIVINE");
    }

    @Test
    @DisplayName("Devuelve el intento con el id generado")
    void saveShouldReturnIntentoWithGeneratedId() {
        Intento guardado = intentoRepository.save(intento(USUARIO_ID));

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getRespuestas()).hasSize(2);
    }

    @Test
    @DisplayName("Lanza DataIntegrityViolationException cuando el usuario no existe")
    void saveShouldThrowWhenUsuarioDoesNotExist() {
        Intento intento = intento(UUID.randomUUID());

        assertThatThrownBy(() -> intentoRepository.save(intento))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    // --- helpers ---
    private Intento intento(UUID usuarioId) {
        return Intento.builder()
                .usuarioId(usuarioId)
                .actividadId(ACTIVIDAD_ID)
                .modo(ModoIntento.PRACTICA)
                .puntaje(PUNTAJE)
                .aprobado(false)
                .iniciadoEn(INICIADO_EN)
                .terminadoEn(TERMINADO_EN)
                .respuestas(List.of(
                        respuesta(PRIMERA_PREGUNTA_ID, true, Confianza.SABIA),
                        respuesta(SEGUNDA_PREGUNTA_ID, false, Confianza.ADIVINE)))
                .build();
    }

    private Respuesta respuesta(UUID preguntaId, boolean correcta, Confianza confianza) {
        return Respuesta.builder()
                .preguntaId(preguntaId)
                .elegida((short) 0)
                .correcta(correcta)
                .confianza(confianza)
                .milisegundos(1200)
                .build();
    }
}
