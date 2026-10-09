package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
@Sql({"/db/rutas-test.sql","/db/retos-test.sql"})
class IRubricaCriterioJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private IRubricaCriterioJpaRepository repository;
    @Test void ordenaAunqueSeInsertenInvertidosYOcultaCriteriosPrivados() {
        var core = UUID.fromString("d1000000-0000-4000-8000-000000000001");
        var result = repository.findGlobalesByRubricaIds(List.of(core,UUID.fromString("d1000000-0000-4000-8000-000000000002")));
        assertThat(result).extracting("nombre").containsExactly("Claridad","Empatía","Próximo paso","Política");
        assertThat(result.getLast().isEliminatorio()).isTrue(); assertThat(result.getFirst().getPeso()).isEqualByComparingTo("1");
        assertThat(result.getFirst().getDescripcion()).isNull();
    }
    @Test void rubricaSinCriteriosDevuelveListaVacia() {
        assertThat(repository.findGlobalesByRubricaIds(List.of(UUID.fromString("d1000000-0000-4000-8000-000000000003")))).isEmpty();
    }
}
