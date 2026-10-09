package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

class INodoPrerrequisitoJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private INodoPrerrequisitoJpaRepository repository;
    @Test void conservaPrerrequisitosValidosDeOtroHitoYExcluyeRelacionesAjenas() {
        var ruta = UUID.fromString("00000000-0000-0000-0000-000000000201");
        var nodo = UUID.fromString("00000000-0000-0000-0000-000000000503");
        assertThat(repository.findDeNodosEnRuta(List.of(nodo), ruta)).extracting("prerrequisitoId")
                .containsExactly(UUID.fromString("00000000-0000-0000-0000-000000000501"), UUID.fromString("00000000-0000-0000-0000-000000000502"));
    }
    @Test void nodoSinPrerrequisitosDevuelveListaVacia() {
        assertThat(repository.findDeNodosEnRuta(List.of(UUID.fromString("00000000-0000-0000-0000-000000000501")),
                UUID.fromString("00000000-0000-0000-0000-000000000201"))).isEmpty();
    }
}
