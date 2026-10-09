package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.infrastructure.repository.entity.RutaEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IRutaJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUTA_PUBLICADA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID RUTA_PRIVADA_ID = UUID.fromString("00000000-0000-0000-0000-000000000208");
    private static final UUID RUTA_SEGUNDA_PAGINA_ID = UUID.fromString("00000000-0000-0000-0000-000000000202");
    private static final String SLUG_CAMBIO_RUBRO = "atencion-test";
    private static final long TOTAL_PUBLICADAS_GLOBALES = 4;
    private static final Sort ORDEN_ESTABLE = Sort.by("titulo", "id");

    @Autowired
    private IRutaJpaRepository repository;

    @Test
    @DisplayName("Combina los filtros de tipo y objetivo y excluye rutas privadas y no publicadas")
    void findPublicadasGlobalesShouldCombineFiltersAndExcludePrivateAndUnpublished() {
        Page<RutaEntity> result = findPublicadasGlobales(
                TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR, PageRequest.of(0, 20, ORDEN_ESTABLE));

        assertThat(result.getContent()).extracting(RutaEntity::getSlug).containsExactly(SLUG_CAMBIO_RUBRO);
    }

    @Test
    @DisplayName("Sin filtros mantiene el orden por título e id y el total de publicadas globales")
    void findPublicadasGlobalesShouldKeepOrderAndTotalWhenNoFilters() {
        Pageable pageable = PageRequest.of(1, 1, ORDEN_ESTABLE);

        Page<RutaEntity> result = findPublicadasGlobales(null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(TOTAL_PUBLICADAS_GLOBALES);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(RUTA_SEGUNDA_PAGINA_ID);
    }

    @Test
    @DisplayName("Devuelve la ruta cuando es global y está publicada")
    void findPublicadaGlobalByIdShouldReturnRutaWhenGlobalAndPublished() {
        Optional<RutaEntity> result = repository.findPublicadaGlobalById(RUTA_PUBLICADA_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(RUTA_PUBLICADA_ID);
    }

    @Test
    @DisplayName("No devuelve la ruta cuando pertenece a una organización")
    void findPublicadaGlobalByIdShouldReturnEmptyWhenRutaIsPrivate() {
        Optional<RutaEntity> result = repository.findPublicadaGlobalById(RUTA_PRIVADA_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Devuelve true cuando la ruta es global y está publicada")
    void existsPublicadaGlobalByIdShouldReturnTrueWhenGlobalAndPublished() {
        boolean result = repository.existsPublicadaGlobalById(RUTA_PUBLICADA_ID);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000-0000-0000-0000-000000000205",
            "00000000-0000-0000-0000-000000000206",
            "00000000-0000-0000-0000-000000000207"
    })
    @DisplayName("Devuelve false cuando la ruta es borrador, en armado o archivada")
    void existsPublicadaGlobalByIdShouldReturnFalseWhenRutaIsNotPublished(String id) {
        boolean result = repository.existsPublicadaGlobalById(UUID.fromString(id));

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Devuelve false cuando la ruta publicada pertenece a una organización")
    void existsPublicadaGlobalByIdShouldReturnFalseWhenRutaIsPrivate() {
        boolean result = repository.existsPublicadaGlobalById(RUTA_PRIVADA_ID);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Devuelve false cuando la ruta no existe")
    void existsPublicadaGlobalByIdShouldReturnFalseWhenRutaDoesNotExist() {
        boolean result = repository.existsPublicadaGlobalById(UUID.randomUUID());

        assertThat(result).isFalse();
    }

    // --- act ---
    private Page<RutaEntity> findPublicadasGlobales(TipoRuta tipo, ObjetivoRuta objetivo, Pageable pageable) {
        return repository.findPublicadasGlobales(tipo, objetivo, pageable);
    }
}
