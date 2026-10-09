package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.infrastructure.repository.entity.RutaEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRutaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RutaRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RutaRepositoryImplTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final String SLUG = "atencion-al-cliente";
    private static final String TITULO = "Atención al cliente";
    private static final Pageable PAGEABLE = PageRequest.of(1, 2);
    private static final long TOTAL_ELEMENTS = 5;

    @Mock
    private IRutaJpaRepository jpa;

    private RutaRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new RutaRepositoryImpl(jpa, new RutaRepositoryMapper());
    }

    @Test
    @DisplayName("Devuelve la ruta mapeada a dominio cuando existe una publicada global con ese id")
    void findPublicadaGlobalByIdShouldReturnMappedRutaWhenPresent() {
        givenJpaFindsEntity();

        Optional<Ruta> result = findById(RUTA_ID);

        thenResultIsMappedRuta(result);
    }

    @Test
    @DisplayName("Devuelve vacío cuando no existe una publicada global con ese id")
    void findPublicadaGlobalByIdShouldReturnEmptyWhenAbsent() {
        givenJpaFindsNothing();

        Optional<Ruta> result = findById(RUTA_ID);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Devuelve la página con las rutas mapeadas a dominio")
    void findPublicadasGlobalesShouldReturnMappedContent() {
        givenJpaReturnsPage(TipoRuta.TECNICA, ObjetivoRuta.ARRANCAR);

        Page<Ruta> result = findPublicadasGlobales(TipoRuta.TECNICA, ObjetivoRuta.ARRANCAR);

        thenPageHasMappedContent(result);
    }

    @Test
    @DisplayName("Conserva los metadatos de paginación de la página")
    void findPublicadasGlobalesShouldKeepPageMetadata() {
        givenJpaReturnsPage(null, null);

        Page<Ruta> result = findPublicadasGlobales(null, null);

        assertThat(result.getTotalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(result.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("Devuelve true cuando existe una publicada global con ese id")
    void existsPublicadaGlobalByIdShouldReturnTrueWhenJpaSaysExists() {
        givenJpaExists(true);

        boolean result = existsById(RUTA_ID);

        assertThat(result).isTrue();
        verify(jpa).existsPublicadaGlobalById(RUTA_ID);
    }

    @Test
    @DisplayName("Devuelve false cuando no existe una publicada global con ese id")
    void existsPublicadaGlobalByIdShouldReturnFalseWhenJpaSaysNotExists() {
        givenJpaExists(false);

        boolean result = existsById(RUTA_ID);

        assertThat(result).isFalse();
    }

    // --- arrange ---
    private void givenJpaFindsEntity() {
        when(jpa.findPublicadaGlobalById(RUTA_ID)).thenReturn(Optional.of(entity()));
    }

    private void givenJpaFindsNothing() {
        when(jpa.findPublicadaGlobalById(RUTA_ID)).thenReturn(Optional.empty());
    }

    private void givenJpaReturnsPage(TipoRuta tipo, ObjetivoRuta objetivo) {
        when(jpa.findPublicadasGlobales(tipo, objetivo, PAGEABLE))
                .thenReturn(new PageImpl<>(List.of(entity()), PAGEABLE, TOTAL_ELEMENTS));
    }

    private void givenJpaExists(boolean exists) {
        when(jpa.existsPublicadaGlobalById(RUTA_ID)).thenReturn(exists);
    }

    // --- act ---
    private Optional<Ruta> findById(UUID id) {
        return repository.findPublicadaGlobalById(id);
    }

    private Page<Ruta> findPublicadasGlobales(TipoRuta tipo, ObjetivoRuta objetivo) {
        return repository.findPublicadasGlobales(tipo, objetivo, PAGEABLE);
    }

    private boolean existsById(UUID id) {
        return repository.existsPublicadaGlobalById(id);
    }

    // --- assert ---
    private void thenResultIsMappedRuta(Optional<Ruta> result) {
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(RUTA_ID);
        assertThat(result.get().getSlug()).isEqualTo(SLUG);
        assertThat(result.get().getTitulo()).isEqualTo(TITULO);
    }

    private void thenPageHasMappedContent(Page<Ruta> result) {
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(RUTA_ID);
        assertThat(result.getContent().getFirst().getSlug()).isEqualTo(SLUG);
        assertThat(result.getContent().getFirst().getTitulo()).isEqualTo(TITULO);
    }

    // --- helpers ---
    private RutaEntity entity() {
        return RutaEntity.builder()
                .id(RUTA_ID)
                .slug(SLUG)
                .titulo(TITULO)
                .build();
    }
}
