package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import com.xpedia.backend.domain.model.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class IRutaJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private IRutaJpaRepository repository;
    @Test void combinaFiltrosYExcluyePrivadasYNoPublicadas() {
        var result = repository.findPublicadasGlobales(TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR, PageRequest.of(0, 20, Sort.by("titulo", "id")));
        assertThat(result.getContent()).extracting("slug").containsExactly("atencion-test");
        assertThat(repository.findPublicadaGlobalById(UUID.fromString("00000000-0000-0000-0000-000000000208"))).isEmpty();
    }
    @Test void paginaSinFiltrosMantieneOrdenYTotal() {
        var result = repository.findPublicadasGlobales(null, null, PageRequest.of(1, 1, Sort.by("titulo", "id")));
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000202"));
    }
}
