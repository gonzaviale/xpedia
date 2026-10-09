package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.HitoEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IHitoJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID HITO_PROPIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID HITO_AJENO_ID = UUID.fromString("00000000-0000-0000-0000-000000000404");

    @Autowired
    private IHitoJpaRepository repository;

    @Test
    @DisplayName("Ordena los hitos de la ruta por posición sin mezclar rutas")
    void findByRutaIdOrderByPosicionAscIdAscShouldReturnOnlyRutaHitosInOrder() {
        List<HitoEntity> result = findHitosDeLaRuta();

        thenTitulosAreInOrder(result);
    }

    @Test
    @DisplayName("Devuelve true cuando el hito pertenece a la ruta")
    void existsByIdAndRutaIdShouldReturnTrueWhenHitoBelongsToRuta() {
        boolean result = existsHitoEnRuta(HITO_PROPIO_ID);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Devuelve false cuando el hito pertenece a otra ruta")
    void existsByIdAndRutaIdShouldReturnFalseWhenHitoBelongsToOtherRuta() {
        boolean result = existsHitoEnRuta(HITO_AJENO_ID);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Devuelve false cuando el hito no existe")
    void existsByIdAndRutaIdShouldReturnFalseWhenHitoDoesNotExist() {
        boolean result = existsHitoEnRuta(UUID.randomUUID());

        assertThat(result).isFalse();
    }

    // --- act ---
    private List<HitoEntity> findHitosDeLaRuta() {
        return repository.findByRutaIdOrderByPosicionAscIdAsc(RUTA_ID);
    }

    private boolean existsHitoEnRuta(UUID hitoId) {
        return repository.existsByIdAndRutaId(hitoId, RUTA_ID);
    }

    // --- assert ---
    private void thenTitulosAreInOrder(List<HitoEntity> hitos) {
        assertThat(hitos).extracting(HitoEntity::getTitulo)
                .containsExactly("Primer hito", "Segundo hito", "Hito sin nodos");
    }
}
