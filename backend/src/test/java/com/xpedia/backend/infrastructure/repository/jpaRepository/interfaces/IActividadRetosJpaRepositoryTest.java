package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/db/rutas-test.sql", "/db/retos-test.sql"})
class IActividadRetosJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID RETO_APROBADO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000001");
    private static final UUID RETO_CON_FUENTE_PRIVADA_ID = UUID.fromString("d3000000-0000-4000-8000-000000000028");
    private static final String RUBRICA_ESCRITURA_ID = "d1000000-0000-4000-8000-000000000001";

    @Autowired
    private IActividadJpaRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("Lista solo retos aprobados de tipo válido con rúbrica global, contenido y fuentes válidas, en orden estable")
    void findRetosAprobadosShouldReturnOnlyVisibleRetosInStableOrder() {
        List<ActividadEntity> result = findRetosAprobados(NODO_ID);

        assertThat(result).extracting(ActividadEntity::getTipo)
                .containsExactly("ENSAYO", "RETO_PROYECTO", "DESAFIO_REAL");
    }

    @Test
    @DisplayName("Devuelve lista vacía para un nodo que no pertenece a la ruta")
    void findRetosAprobadosShouldReturnEmptyWhenNodoIsNotInRuta() {
        List<ActividadEntity> result = findRetosAprobados(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Incluye los retos cuando el umbral de aprobación es cero")
    void findRetosAprobadosShouldIncludeRetosWhenThresholdIsZero() {
        givenRubricaThreshold("0");

        List<ActividadEntity> result = findRetosAprobados(NODO_ID);

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Incluye los retos cuando el umbral de aprobación es igual al máximo de la rúbrica")
    void findRetosAprobadosShouldIncludeRetosWhenThresholdEqualsMaximum() {
        givenRubricaThreshold("12");

        List<ActividadEntity> result = findRetosAprobados(NODO_ID);

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Excluye los retos cuando el umbral de aprobación supera el máximo de la rúbrica")
    void findRetosAprobadosShouldExcludeRetosWhenThresholdExceedsMaximum() {
        givenRubricaThreshold("12.01");

        List<ActividadEntity> result = findRetosAprobados(NODO_ID);

        assertThat(result).extracting(ActividadEntity::getTipo).containsExactly("RETO_PROYECTO");
    }

    @Test
    @DisplayName("Encuentra un reto aprobado por id dentro del nodo")
    void findRetoAprobadoByIdShouldReturnRetoWhenVisible() {
        Optional<ActividadEntity> result = findRetoAprobadoById(RETO_APROBADO_ID);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("No encuentra un reto con una fuente privada asociada")
    void findRetoAprobadoByIdShouldReturnEmptyWhenRetoHasPrivateSource() {
        Optional<ActividadEntity> result = findRetoAprobadoById(RETO_CON_FUENTE_PRIVADA_ID);

        assertThat(result).isEmpty();
    }

    // --- arrange ---
    private void givenRubricaThreshold(String puntajeAprobacion) {
        jdbc.update("UPDATE rubrica SET puntaje_aprobacion=" + puntajeAprobacion
                + " WHERE id='" + RUBRICA_ESCRITURA_ID + "'");
    }

    // --- act ---
    private List<ActividadEntity> findRetosAprobados(UUID nodoId) {
        return repository.findRetosAprobados(RUTA_ID, nodoId);
    }

    private Optional<ActividadEntity> findRetoAprobadoById(UUID retoId) {
        return repository.findRetoAprobadoById(RUTA_ID, NODO_ID, retoId);
    }
}
