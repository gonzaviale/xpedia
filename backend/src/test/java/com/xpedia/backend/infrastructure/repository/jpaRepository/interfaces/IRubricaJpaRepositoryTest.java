package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
@Sql({"/db/rutas-test.sql","/db/retos-test.sql"})
class IRubricaJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private IRubricaJpaRepository repository;
    @Test void loteExcluyeRubricasPrivadasYConservaDecimalesYNulos() {
        var id = UUID.fromString("d1000000-0000-4000-8000-000000000008");
        var result = repository.findGlobalesByIds(List.of(id,UUID.fromString("d1000000-0000-4000-8000-000000000002")));
        assertThat(result).hasSize(1); assertThat(result.getFirst().getId()).isEqualTo(id);
        assertThat(result.getFirst().getPuntajeAprobacion()).isEqualByComparingTo("2.25"); assertThat(result.getFirst().getDescripcion()).isNull();
    }
    @Test void idAusenteDevuelveListaVacia() { assertThat(repository.findGlobalesByIds(List.of(UUID.randomUUID()))).isEmpty(); }
}
