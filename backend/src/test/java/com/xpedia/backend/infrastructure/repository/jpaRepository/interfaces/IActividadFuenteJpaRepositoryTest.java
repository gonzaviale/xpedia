package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadFuenteJpaRepository.FuenteVisible;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/db/rutas-test.sql", "/db/microlecciones-test.sql"})
class IActividadFuenteJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID MICROLECCION_INICIAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000911");
    private static final UUID MICROLECCION_FUENTE_MIXTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000916");
    private static final String FUENTE_GLOBAL_TITULO = "A Fuente global";
    private static final String FUENTE_ENLACE_TITULO = "B Enlace global";
    private static final String USO_SOLO_ENLACE = "SOLO_ENLACE";
    private static final String LICENCIA_SOLO_REFERENCIA = "Solo referencia";

    @Autowired
    private IActividadFuenteJpaRepository repository;

    @Test
    @DisplayName("Devuelve las fuentes globales ordenadas por título y oculta las fuentes privadas")
    void findVisiblesDeActividadesShouldReturnGlobalFuentesOrderedAndHidePrivateOnes() {
        List<FuenteVisible> result = findVisibles(MICROLECCION_INICIAL_ID, MICROLECCION_FUENTE_MIXTA_ID);

        assertThat(result).extracting(FuenteVisible::getTitulo)
                .containsExactly(FUENTE_GLOBAL_TITULO, FUENTE_GLOBAL_TITULO, FUENTE_ENLACE_TITULO);
    }

    @Test
    @DisplayName("Conserva los nulos y los permisos de una fuente solo enlace")
    void findVisiblesDeActividadesShouldKeepNullsAndPermissionsOfLinkOnlyFuente() {
        List<FuenteVisible> result = findVisibles(MICROLECCION_INICIAL_ID, MICROLECCION_FUENTE_MIXTA_ID);

        thenLinkOnlyFuenteKeepsNullsAndPermissions(result);
    }

    @Test
    @DisplayName("Devuelve lista vacía cuando la actividad no existe")
    void findVisiblesDeActividadesShouldReturnEmptyListWhenActividadDoesNotExist() {
        List<FuenteVisible> result = findVisibles(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    // --- act ---
    private List<FuenteVisible> findVisibles(UUID... actividadIds) {
        return repository.findVisiblesDeActividades(List.of(actividadIds));
    }

    // --- assert ---
    private void thenLinkOnlyFuenteKeepsNullsAndPermissions(List<FuenteVisible> result) {
        FuenteVisible enlace = result.stream()
                .filter(fuente -> USO_SOLO_ENLACE.equals(fuente.getUso()))
                .findFirst()
                .orElseThrow();
        assertThat(enlace.getUrl()).isNull();
        assertThat(enlace.getUbicacion()).isNull();
        assertThat(enlace.getPermiteUsoComercial()).isFalse();
        assertThat(enlace.getLicencia()).isEqualTo(LICENCIA_SOLO_REFERENCIA);
    }
}
