package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.ProgresoNodoEntity;
import com.xpedia.backend.infrastructure.repository.entity.ProgresoNodoId;
import com.xpedia.backend.infrastructure.repository.mapper.ProgresoNodoRepositoryMapper;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.NODO_ID;
import static com.xpedia.backend.support.InscripcionTestData.progreso;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql({"/db/inscripciones-cleanup.sql", "/db/rutas-test.sql", "/db/usuarios-test.sql", "/db/inscripciones-test.sql"})
class IProgresoNodoJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID OTRA_INSCRIPCION_ID = UUID.fromString("c1000000-0000-4000-8000-000000000003");

    @Autowired
    private IProgresoNodoJpaRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanup() {
        jdbcTemplate.execute("DELETE FROM inscripcion");
    }

    @Test
    @DisplayName("Ordena por hito, nodo e ID y deja los temas sin hito al final")
    void findByInscripcionIdShouldOrderAndIsolateProgress() {
        List<ProgresoNodoEntity> result = repository.findByInscripcionIdOrdenado(INSCRIPCION_ID);

        thenFindByInscripcionIdShouldOrderAndIsolateProgress(result);
    }

    @Test
    @DisplayName("El mismo nodo mantiene progresos independientes por inscripción")
    void findByIdShouldSeparateCompositeKeys() {
        var primero = repository.findById(new ProgresoNodoId(INSCRIPCION_ID, NODO_ID)).orElseThrow();
        var otro = repository.findById(new ProgresoNodoId(OTRA_INSCRIPCION_ID, NODO_ID)).orElseThrow();

        thenFindByIdShouldSeparateCompositeKeys(primero, otro);
    }

    @Test
    @DisplayName("Guarda dominio, nivel, fallos y fechas sin perder precisión")
    void saveShouldPersistAllProgressFields() {
        ProgresoNodoEntity entity = new ProgresoNodoRepositoryMapper().toEntity(progreso());

        repository.saveAndFlush(entity);
        ProgresoNodoEntity result = repository.findById(new ProgresoNodoId(INSCRIPCION_ID, NODO_ID)).orElseThrow();

        thenPersistedFields(result, entity);
    }

    @Test
    @DisplayName("La base rechaza un dominio superior a uno")
    void saveShouldRejectOutOfRangeMastery() {
        ProgresoNodoEntity entity = new ProgresoNodoRepositoryMapper().toEntity(progreso());
        entity.setDominio(new BigDecimal("1.01"));

        thenSaveShouldRejectOutOfRangeMastery(entity);
    }

    @Test
    @DisplayName("Sin inscripción devuelve lista vacía")
    void findByInscripcionIdShouldReturnEmptyWhenMissing() {
        List<ProgresoNodoEntity> result = repository.findByInscripcionIdOrdenado(UUID.randomUUID());

        thenFindByInscripcionIdShouldReturnEmptyWhenMissing(result);
    }

    // --- assert ---
    private void thenPersistedFields(ProgresoNodoEntity result, ProgresoNodoEntity expected) {
        assertThat(result.getInscripcionId()).isEqualTo(expected.getInscripcionId());
        assertThat(result.getNodoId()).isEqualTo(expected.getNodoId());
        assertThat(result.getEstado()).isEqualTo(expected.getEstado());
        assertThat(result.getDominio()).isEqualTo(expected.getDominio());
        assertThat(result.getNivel()).isEqualTo(expected.getNivel());
        assertThat(result.getCantidadFallos()).isEqualTo(expected.getCantidadFallos());
        assertThat(result.getCreadoEn()).isEqualTo(expected.getCreadoEn());
        assertThat(result.getActualizadoEn()).isEqualTo(expected.getActualizadoEn());
    }

    private void thenFindByInscripcionIdShouldOrderAndIsolateProgress(List<ProgresoNodoEntity> result) {
        assertThat(result).extracting(p -> p.getNodoId().toString()).containsExactly(
                "00000000-0000-0000-0000-000000000501", "00000000-0000-0000-0000-000000000502",
                "00000000-0000-0000-0000-000000000503", "00000000-0000-0000-0000-000000000504");
        assertThat(result).allSatisfy(p -> assertThat(p.getInscripcionId()).isEqualTo(INSCRIPCION_ID));
    }

    private void thenFindByIdShouldSeparateCompositeKeys(ProgresoNodoEntity primero, ProgresoNodoEntity otro) {
        assertThat(primero.getDominio()).isZero();
        assertThat(otro.getDominio()).isEqualByComparingTo("0.65");
    }

    private void thenSaveShouldRejectOutOfRangeMastery(ProgresoNodoEntity entity) {
        assertThatThrownBy(() -> repository.saveAndFlush(entity))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private void thenFindByInscripcionIdShouldReturnEmptyWhenMissing(List<ProgresoNodoEntity> result) {
        assertThat(result).isEmpty();
    }
}
