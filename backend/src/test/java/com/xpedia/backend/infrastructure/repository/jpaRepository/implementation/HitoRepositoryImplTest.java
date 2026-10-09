package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.enums.EstadoPropuestaHito;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.infrastructure.repository.entity.HitoEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IHitoJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.HitoRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HitoRepositoryImplTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID PRIMER_HITO_ID = UUID.randomUUID();
    private static final UUID SEGUNDO_HITO_ID = UUID.randomUUID();
    private static final String PRIMER_TITULO = "Primer hito";
    private static final String SEGUNDO_TITULO = "Segundo hito";
    private static final String OBJETIVO = "Aprender lo básico";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("8.5");
    private static final String EVIDENCIA_ESPERADA = "Un informe";
    private static final String COMENTARIO_AJUSTE = "Acortar el hito";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-01-02T10:00:00Z");

    @Mock
    private IHitoJpaRepository jpa;

    private HitoRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new HitoRepositoryImpl(jpa, new HitoRepositoryMapper());
    }

    @Test
    @DisplayName("Devuelve los hitos de la ruta con todos sus campos mapeados")
    void findByRutaIdShouldReturnMappedHitos() {
        givenJpaReturnsHitos(List.of(hitoEntity(PRIMER_HITO_ID, PRIMER_TITULO)));

        List<Hito> result = findByRutaId();

        thenResultHasMappedHito(result.getFirst());
    }

    @Test
    @DisplayName("Conserva el orden que devuelve la consulta")
    void findByRutaIdShouldKeepQueryOrder() {
        givenJpaReturnsHitos(List.of(
                hitoEntity(PRIMER_HITO_ID, PRIMER_TITULO),
                hitoEntity(SEGUNDO_HITO_ID, SEGUNDO_TITULO)));

        List<Hito> result = findByRutaId();

        assertThat(result).extracting(Hito::getId).containsExactly(PRIMER_HITO_ID, SEGUNDO_HITO_ID);
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando la ruta no tiene hitos")
    void findByRutaIdShouldReturnEmptyListWhenRutaHasNoHitos() {
        givenJpaReturnsHitos(List.of());

        List<Hito> result = findByRutaId();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Devuelve true cuando el hito pertenece a la ruta")
    void existsByIdAndRutaIdShouldReturnTrueWhenHitoBelongsToRuta() {
        givenJpaExistence(true);

        boolean result = existsByIdAndRutaId();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Devuelve false cuando el hito no pertenece a la ruta")
    void existsByIdAndRutaIdShouldReturnFalseWhenHitoDoesNotBelongToRuta() {
        givenJpaExistence(false);

        boolean result = existsByIdAndRutaId();

        assertThat(result).isFalse();
    }

    // --- arrange ---
    private void givenJpaReturnsHitos(List<HitoEntity> entities) {
        when(jpa.findByRutaIdOrderByPosicionAscIdAsc(RUTA_ID)).thenReturn(entities);
    }

    private void givenJpaExistence(boolean exists) {
        when(jpa.existsByIdAndRutaId(PRIMER_HITO_ID, RUTA_ID)).thenReturn(exists);
    }

    // --- helpers ---
    private HitoEntity hitoEntity(UUID id, String titulo) {
        return HitoEntity.builder()
                .id(id)
                .rutaId(RUTA_ID)
                .posicion((short) 1)
                .titulo(titulo)
                .objetivo(OBJETIVO)
                .horasEstimadas(HORAS_ESTIMADAS)
                .esFinal(true)
                .evidenciaEsperada(EVIDENCIA_ESPERADA)
                .estadoPropuesta(EstadoPropuestaHito.A_AJUSTAR)
                .comentarioAjuste(COMENTARIO_AJUSTE)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    // --- act ---
    private List<Hito> findByRutaId() {
        return repository.findByRutaId(RUTA_ID);
    }

    private boolean existsByIdAndRutaId() {
        return repository.existsByIdAndRutaId(PRIMER_HITO_ID, RUTA_ID);
    }

    // --- assert ---
    private void thenResultHasMappedHito(Hito hito) {
        assertThat(hito.getId()).isEqualTo(PRIMER_HITO_ID);
        assertThat(hito.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(hito.getPosicion()).isEqualTo((short) 1);
        assertThat(hito.getTitulo()).isEqualTo(PRIMER_TITULO);
        assertThat(hito.getObjetivo()).isEqualTo(OBJETIVO);
        assertThat(hito.getHorasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(hito.getEsFinal()).isTrue();
        assertThat(hito.getEvidenciaEsperada()).isEqualTo(EVIDENCIA_ESPERADA);
        assertThat(hito.getEstadoPropuesta()).isEqualTo(EstadoPropuestaHito.A_AJUSTAR);
        assertThat(hito.getComentarioAjuste()).isEqualTo(COMENTARIO_AJUSTE);
        assertThat(hito.getCreadoEn()).isEqualTo(CREADO_EN);
        assertThat(hito.getActualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }
}
