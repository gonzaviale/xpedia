package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/db/rutas-test.sql", "/db/microlecciones-test.sql"})
class IActividadJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private IActividadJpaRepository repository;
    @Autowired private JdbcTemplate jdbc;
    private final UUID ruta = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private final UUID nodo = UUID.fromString("00000000-0000-0000-0000-000000000501");

    @Test void filtraTipoRevisionFuentesYRelacionesAntesDeMapearJsonb() {
        var result = repository.findMicroleccionesAprobadas(ruta, nodo);
        assertThat(result).extracting("titulo").containsExactly("Lección inicial", "Lección posterior");
        assertThat(result.getFirst().getContenido()).containsEntry("texto", "Comunicación ñ");
        assertThat(repository.findMicroleccionesAprobadas(ruta, UUID.randomUUID())).isEmpty();
    }
    @Test void idsDesempatanMismoNivelYFecha() {
        jdbc.update("UPDATE actividad SET nivel=1, creado_en='2026-10-01T10:00:00Z' WHERE id='00000000-0000-0000-0000-000000000912'");
        assertThat(repository.findMicroleccionesAprobadas(ruta, nodo)).extracting("id")
                .containsExactly(UUID.fromString("00000000-0000-0000-0000-000000000911"), UUID.fromString("00000000-0000-0000-0000-000000000912"));
    }
}
