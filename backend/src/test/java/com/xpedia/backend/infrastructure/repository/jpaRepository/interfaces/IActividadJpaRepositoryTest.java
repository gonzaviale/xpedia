package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/db/rutas-test.sql", "/db/microlecciones-test.sql"})
class IActividadJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID MICROLECCION_INICIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000911");
    private static final UUID MICROLECCION_POSTERIOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000912");
    private static final String TITULO_INICIAL = "Lección inicial";
    private static final String TITULO_POSTERIOR = "Lección posterior";
    private static final String UPDATE_MISMO_NIVEL_Y_FECHA = """
            UPDATE actividad SET nivel=1, creado_en='2026-10-01T10:00:00Z'
            WHERE id='00000000-0000-0000-0000-000000000912'
            """;

    @Autowired
    private IActividadJpaRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    @DisplayName("Devuelve solo las microlecciones aprobadas con fuente global, ordenadas por nivel")
    void findMicroleccionesAprobadasShouldReturnOnlyApprovedWithGlobalFuenteOrderedByNivel() {
        List<ActividadEntity> result = findAprobadas(NODO_ID);

        assertThat(result).extracting(ActividadEntity::getTitulo)
                .containsExactly(TITULO_INICIAL, TITULO_POSTERIOR);
    }

    @Test
    @DisplayName("Mapea el contenido jsonb con sus caracteres especiales")
    void findMicroleccionesAprobadasShouldMapJsonbContent() {
        List<ActividadEntity> result = findAprobadas(NODO_ID);

        assertThat(result.getFirst().getContenido()).containsEntry("texto", "Comunicación ñ");
    }

    @Test
    @DisplayName("Devuelve lista vacía cuando el nodo no existe")
    void findMicroleccionesAprobadasShouldReturnEmptyListWhenNodoDoesNotExist() {
        List<ActividadEntity> result = findAprobadas(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Desempata por id cuando coinciden nivel y fecha de creación")
    void findMicroleccionesAprobadasShouldBreakTiesById() {
        givenSameNivelAndCreationDate();

        List<ActividadEntity> result = findAprobadas(NODO_ID);

        assertThat(result).extracting(ActividadEntity::getId)
                .containsExactly(MICROLECCION_INICIAL_ID, MICROLECCION_POSTERIOR_ID);
    }

    // --- arrange ---
    private void givenSameNivelAndCreationDate() {
        jdbc.update(UPDATE_MISMO_NIVEL_Y_FECHA);
    }

    // --- act ---
    private List<ActividadEntity> findAprobadas(UUID nodoId) {
        return repository.findMicroleccionesAprobadas(RUTA_ID, nodoId);
    }
}
