package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.NodoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.INodoJpaRepository.ReferenciasVisibles;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class INodoJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID HITO_SEGUNDO_ID = UUID.fromString("00000000-0000-0000-0000-000000000402");
    private static final UUID NODO_VISIBLE_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID NODO_SIN_HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000504");
    private static final UUID NODO_CON_HITO_AJENO_ID = UUID.fromString("00000000-0000-0000-0000-000000000505");
    private static final UUID NODO_DE_OTRA_RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000507");
    private static final UUID NODO_CON_REFERENCIAS_OCULTAS_ID = UUID.fromString("00000000-0000-0000-0000-000000000502");

    @Autowired
    private INodoJpaRepository repository;

    @Test
    @DisplayName("Un nodo con hito de la ruta es visible")
    void existsVisibleByIdAndRutaIdShouldReturnTrueWhenNodoHasHitoOfRuta() {
        boolean result = existsVisible(NODO_VISIBLE_ID);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Un nodo sin hito es visible")
    void existsVisibleByIdAndRutaIdShouldReturnTrueWhenNodoHasNoHito() {
        boolean result = existsVisible(NODO_SIN_HITO_ID);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Un nodo con hito incompatible con la ruta no es visible")
    void existsVisibleByIdAndRutaIdShouldReturnFalseWhenNodoHasHitoOfOtherRuta() {
        boolean result = existsVisible(NODO_CON_HITO_AJENO_ID);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Un nodo de otra ruta no es visible")
    void existsVisibleByIdAndRutaIdShouldReturnFalseWhenNodoBelongsToOtherRuta() {
        boolean result = existsVisible(NODO_DE_OTRA_RUTA_ID);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Un nodo inexistente no es visible")
    void existsVisibleByIdAndRutaIdShouldReturnFalseWhenNodoDoesNotExist() {
        boolean result = existsVisible(UUID.randomUUID());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Ordena los nodos por hito y posición y excluye los de hitos ajenos")
    void findByRutaIdShouldOrderNodosAndExcludeOnesWithForeignHito() {
        List<NodoEntity> result = findNodos(null);

        thenCodigosAre(result, "A1", "A2", "B1", "T1");
    }

    @Test
    @DisplayName("Filtra los nodos por hito")
    void findByRutaIdShouldFilterNodosByHito() {
        List<NodoEntity> result = findNodos(HITO_SEGUNDO_ID);

        thenCodigosAre(result, "B1");
    }

    @Test
    @DisplayName("Oculta ramas ajenas y habilidades privadas en la proyección de referencias")
    void findReferenciasVisiblesShouldHideForeignRamasAndPrivateHabilidades() {
        List<ReferenciasVisibles> referencias = findReferencias();

        thenReferenciasAreHiddenForNodo(referencias, NODO_CON_REFERENCIAS_OCULTAS_ID);
    }

    // --- act ---
    private boolean existsVisible(UUID nodoId) {
        return repository.existsVisibleByIdAndRutaId(nodoId, RUTA_ID);
    }

    private List<NodoEntity> findNodos(UUID hitoId) {
        return repository.findByRutaId(RUTA_ID, hitoId);
    }

    private List<ReferenciasVisibles> findReferencias() {
        List<UUID> ids = findNodos(null).stream().map(NodoEntity::getId).toList();
        return repository.findReferenciasVisibles(RUTA_ID, ids);
    }

    // --- assert ---
    private void thenCodigosAre(List<NodoEntity> nodos, String... codigos) {
        assertThat(nodos).extracting(NodoEntity::getCodigo).containsExactly(codigos);
    }

    private void thenReferenciasAreHiddenForNodo(List<ReferenciasVisibles> referencias, UUID nodoId) {
        ReferenciasVisibles oculta = referencias.stream()
                .filter(referencia -> referencia.getNodoId().equals(nodoId))
                .findFirst()
                .orElseThrow();
        assertThat(oculta.getRamaId()).isNull();
        assertThat(oculta.getHabilidadId()).isNull();
    }
}
