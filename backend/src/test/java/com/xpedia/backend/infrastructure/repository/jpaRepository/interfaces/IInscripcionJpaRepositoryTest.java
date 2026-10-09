package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.InscripcionEntity;
import com.xpedia.backend.infrastructure.repository.mapper.InscripcionRepositoryMapper;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.UUID;

import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.RUTA_ID;
import static com.xpedia.backend.support.InscripcionTestData.USUARIO_ID;
import static com.xpedia.backend.support.InscripcionTestData.inscripcion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql({"/db/inscripciones-cleanup.sql", "/db/rutas-test.sql", "/db/usuarios-test.sql", "/db/inscripciones-test.sql"})
class IInscripcionJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID OTRA_RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000203");

    private static final UUID PAUSADA_RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000202");

    private static final UUID TERMINADA_RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000204");

    private static final UUID MAS_RECIENTE_ID = UUID.fromString("c1000000-0000-4000-8000-000000000002");

    private static final UUID OTRO_USUARIO_ID = UUID.fromString("b9000000-0000-4000-8000-000000000002");

    @Autowired
    private IInscripcionJpaRepository repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanup() {
        jdbcTemplate.execute("DELETE FROM inscripcion");
    }

    @Test
    @DisplayName("ACTIVA y PAUSADA cuentan como abiertas; TERMINADA no")
    void existsAbiertaShouldMatchPartialUniqueIndex() {
        boolean activa = repository.existsAbiertaByUsuarioIdAndRutaId(USUARIO_ID, RUTA_ID);
        boolean pausada = repository.existsAbiertaByUsuarioIdAndRutaId(USUARIO_ID, PAUSADA_RUTA_ID);
        boolean terminada = repository.existsAbiertaByUsuarioIdAndRutaId(USUARIO_ID, TERMINADA_RUTA_ID);

        thenExistsAbiertaShouldMatchPartialUniqueIndex(activa, pausada, terminada);
    }

    @Test
    @DisplayName("Elige la activa más reciente ignorando pausadas, terminadas y otros usuarios")
    void findActualShouldReturnNewestActiveForPerson() {
        var actual = repository.findFirstByUsuarioIdAndEstadoOrderByIniciadaEnDescIdDesc(USUARIO_ID, "ACTIVA");

        thenFindActualShouldReturnNewestActiveForPerson(actual);
    }

    @Test
    @DisplayName("En empate de fecha usa ID descendente para una consulta estable")
    void findActualShouldBreakTimestampTiesById() {
        jdbcTemplate.update("UPDATE inscripcion SET iniciada_en = '2026-10-02T10:00:00Z' WHERE id = ?", INSCRIPCION_ID);

        var actual = repository.findFirstByUsuarioIdAndEstadoOrderByIniciadaEnDescIdDesc(USUARIO_ID, "ACTIVA");

        thenFindActualShouldBreakTimestampTiesById(actual);
    }

    @Test
    @DisplayName("Sin activas no devuelve una pausada ni una terminada")
    void findActualShouldReturnEmptyWhenNoActiveEnrollment() {
        jdbcTemplate.update("UPDATE inscripcion SET estado = 'PAUSADA' WHERE usuario_id = ?", USUARIO_ID);

        var actual = repository.findFirstByUsuarioIdAndEstadoOrderByIniciadaEnDescIdDesc(USUARIO_ID, "ACTIVA");

        thenFindActualShouldReturnEmptyWhenNoActiveEnrollment(actual);
    }

    @Test
    @DisplayName("El índice único impide otra inscripción abierta aun sin validación previa")
    void saveShouldRejectDuplicateOpenEnrollment() {
        InscripcionEntity entity = new InscripcionRepositoryMapper().toEntity(inscripcion());
        entity.setId(UUID.randomUUID());

        thenSaveShouldRejectDuplicateOpenEnrollment(entity);
    }

    @Test
    @DisplayName("Permite una nueva inscripción cuando la anterior está terminada")
    void saveShouldAllowEnrollmentAfterCompletion() {
        jdbcTemplate.update("UPDATE inscripcion SET estado = 'TERMINADA' WHERE id = ?", INSCRIPCION_ID);
        InscripcionEntity entity = new InscripcionRepositoryMapper().toEntity(inscripcion());
        entity.setId(UUID.randomUUID());

        InscripcionEntity saved = repository.saveAndFlush(entity);

        thenPersistedFields(saved, entity);
    }

    // --- assert ---
    private void thenPersistedFields(InscripcionEntity result, InscripcionEntity expected) {
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getUsuarioId()).isEqualTo(expected.getUsuarioId());
        assertThat(result.getRutaId()).isEqualTo(expected.getRutaId());
        assertThat(result.getObjetivo()).isEqualTo(expected.getObjetivo());
        assertThat(result.getMetaPersonal()).isEqualTo(expected.getMetaPersonal());
        assertThat(result.getRitmoMin()).isEqualTo(expected.getRitmoMin());
        assertThat(result.getFechaLlegadaEstimada()).isEqualTo(expected.getFechaLlegadaEstimada());
        assertThat(result.getEstado()).isEqualTo(expected.getEstado());
        assertThat(result.getHitoActualId()).isEqualTo(expected.getHitoActualId());
        assertThat(result.getIniciadaEn()).isEqualTo(expected.getIniciadaEn());
        assertThat(result.getCreadoEn()).isEqualTo(expected.getCreadoEn());
        assertThat(result.getActualizadoEn()).isEqualTo(expected.getActualizadoEn());
    }

    private void thenExistsAbiertaShouldMatchPartialUniqueIndex(boolean activa, boolean pausada, boolean terminada) {
        assertThat(activa).isTrue();
        assertThat(pausada).isTrue();
        assertThat(terminada).isFalse();
        assertThat(repository.existsAbiertaByUsuarioIdAndRutaId(OTRO_USUARIO_ID, OTRA_RUTA_ID)).isFalse();
    }

    private void thenFindActualShouldReturnNewestActiveForPerson(Optional<InscripcionEntity> actual) {
        assertThat(actual.orElseThrow().getId()).isEqualTo(MAS_RECIENTE_ID);
    }

    private void thenFindActualShouldBreakTimestampTiesById(Optional<InscripcionEntity> actual) {
        assertThat(actual.orElseThrow().getId()).isEqualTo(MAS_RECIENTE_ID);
    }

    private void thenFindActualShouldReturnEmptyWhenNoActiveEnrollment(Optional<InscripcionEntity> actual) {
        assertThat(actual).isEmpty();
    }

    private void thenSaveShouldRejectDuplicateOpenEnrollment(InscripcionEntity entity) {
        assertThatThrownBy(() -> repository.saveAndFlush(entity))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
