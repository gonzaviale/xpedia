package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/db/rutas-test.sql", "/db/microlecciones-test.sql"})
class IActividadFuenteJpaRepositoryTest extends PostgresRepositoryTestSupport {
    @Autowired private IActividadFuenteJpaRepository repository;
    @Test void consultaEnLoteConservaMetadatosYNulosYOcultaFuentesPrivadas() {
        var ids = List.of(UUID.fromString("00000000-0000-0000-0000-000000000911"), UUID.fromString("00000000-0000-0000-0000-000000000916"));
        var result = repository.findVisiblesDeActividades(ids);
        assertThat(result).hasSize(3); assertThat(result).extracting(IActividadFuenteJpaRepository.FuenteVisible::getTitulo)
                .containsExactly("A Fuente global", "A Fuente global", "B Enlace global");
        var enlace = result.stream().filter(f -> f.getUso().equals("SOLO_ENLACE")).findFirst().orElseThrow();
        assertThat(enlace.getUrl()).isNull(); assertThat(enlace.getUbicacion()).isNull(); assertThat(enlace.getPermiteUsoComercial()).isFalse();
        assertThat(enlace.getLicencia()).isEqualTo("Solo referencia");
    }
    @Test void actividadAusenteNoDevuelveFuentes() {
        assertThat(repository.findVisiblesDeActividades(List.of(UUID.randomUUID()))).isEmpty();
    }
}
