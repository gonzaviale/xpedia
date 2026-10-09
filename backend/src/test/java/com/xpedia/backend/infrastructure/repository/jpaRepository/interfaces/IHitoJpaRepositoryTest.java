package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class IHitoJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private IHitoJpaRepository repository;
    private final UUID ruta = UUID.fromString("00000000-0000-0000-0000-000000000201");
    @Test void ordenaHitosSinMezclarRutas() {
        assertThat(repository.findByRutaIdOrderByPosicionAscIdAsc(ruta)).extracting("titulo")
                .containsExactly("Primer hito", "Segundo hito", "Hito sin nodos");
    }
    @Test void pertenenciaDistingueHitoPropioAjenoYAusente() {
        assertThat(repository.existsByIdAndRutaId(UUID.fromString("00000000-0000-0000-0000-000000000401"), ruta)).isTrue();
        assertThat(repository.existsByIdAndRutaId(UUID.fromString("00000000-0000-0000-0000-000000000404"), ruta)).isFalse();
        assertThat(repository.existsByIdAndRutaId(UUID.randomUUID(), ruta)).isFalse();
    }
}
