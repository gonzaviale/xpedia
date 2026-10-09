package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class INodoJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private INodoJpaRepository repository;
    private final UUID ruta = UUID.fromString("00000000-0000-0000-0000-000000000201");
    @Test void ordenaYExcluyeNodosConHitosAjenos() {
        assertThat(repository.findByRutaId(ruta, null)).extracting("codigo").containsExactly("A1", "A2", "B1", "T1");
        assertThat(repository.findByRutaId(ruta, UUID.fromString("00000000-0000-0000-0000-000000000402"))).extracting("codigo").containsExactly("B1");
    }
    @Test void proyeccionOcultaRamasAjenasYHabilidadesPrivadas() {
        var nodes = repository.findByRutaId(ruta, null);
        var refs = repository.findReferenciasVisibles(ruta, nodes.stream().map(n -> n.getId()).toList());
        var hidden = refs.stream().filter(r -> r.getNodoId().toString().endsWith("502")).findFirst().orElseThrow();
        assertThat(hidden.getRamaId()).isNull(); assertThat(hidden.getHabilidadId()).isNull();
    }
}
