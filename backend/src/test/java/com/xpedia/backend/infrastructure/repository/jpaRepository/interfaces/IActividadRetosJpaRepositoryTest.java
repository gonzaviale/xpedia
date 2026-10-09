package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
@Sql({"/db/rutas-test.sql","/db/retos-test.sql"})
class IActividadRetosJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private IActividadJpaRepository repository;
    @Autowired private JdbcTemplate jdbc;
    private final UUID ruta = UUID.fromString("00000000-0000-0000-0000-000000000201"), nodo = UUID.fromString("00000000-0000-0000-0000-000000000501");
    @Test void filtraRevisionTiposJsonRubricasFuentesYPertenenciaConOrdenEstable() {
        assertThat(repository.findRetosAprobados(ruta,nodo)).extracting("tipo").containsExactly("ENSAYO","RETO_PROYECTO","DESAFIO_REAL");
        assertThat(repository.findRetoAprobadoById(ruta,nodo,UUID.fromString("d3000000-0000-4000-8000-000000000001"))).isPresent();
        assertThat(repository.findRetoAprobadoById(ruta,nodo,UUID.fromString("d3000000-0000-4000-8000-000000000028"))).isEmpty();
        assertThat(repository.findRetosAprobados(ruta,UUID.randomUUID())).isEmpty();
    }
    @Test void admiteUmbralCeroYMaximoExactoPeroExcluyeMayor() {
        jdbc.update("UPDATE rubrica SET puntaje_aprobacion=0 WHERE id='d1000000-0000-4000-8000-000000000001'");
        assertThat(repository.findRetosAprobados(ruta,nodo)).hasSize(3);
        jdbc.update("UPDATE rubrica SET puntaje_aprobacion=12 WHERE id='d1000000-0000-4000-8000-000000000001'");
        assertThat(repository.findRetosAprobados(ruta,nodo)).hasSize(3);
        jdbc.update("UPDATE rubrica SET puntaje_aprobacion=12.01 WHERE id='d1000000-0000-4000-8000-000000000001'");
        assertThat(repository.findRetosAprobados(ruta,nodo)).extracting("tipo").containsExactly("RETO_PROYECTO");
    }
}
