package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.rubrica.CriterioRubrica;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import com.xpedia.backend.infrastructure.repository.entity.RubricaCriterioEntity;
import com.xpedia.backend.infrastructure.repository.entity.RubricaEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRubricaCriterioJpaRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IRubricaJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.RubricaRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RubricaRepositoryImplTest {

    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID OTRA_RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000003");
    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");
    private static final List<UUID> IDS = List.of(RUBRICA_ID, OTRA_RUBRICA_ID);

    @Mock
    private IRubricaJpaRepository rubricas;

    @Mock
    private IRubricaCriterioJpaRepository criterios;

    private RubricaRepositoryImpl rubricaRepositoryImpl;

    @BeforeEach
    void setUp() {
        rubricaRepositoryImpl = new RubricaRepositoryImpl(rubricas, criterios, new RubricaRepositoryMapper());
    }

    @Test
    @DisplayName("Devuelve un mapa vacío sin consultar la base cuando no se piden ids")
    void findGlobalesByIdsShouldReturnEmptyWithoutQueryingWhenIdsAreEmpty() {
        Map<UUID, Rubrica> result = findGlobalesByIds(List.of());

        thenResultIsEmptyAndNothingQueried(result);
    }

    @Test
    @DisplayName("Devuelve un mapa vacío sin consultar criterios cuando ninguna rúbrica es global")
    void findGlobalesByIdsShouldNotQueryCriteriosWhenNoGlobalRubricas() {
        givenGlobalRubricas(List.of());

        Map<UUID, Rubrica> result = findGlobalesByIds(IDS);

        assertThat(result).isEmpty();
        verifyNoInteractions(criterios);
    }

    @Test
    @DisplayName("Mapea la rúbrica global con sus criterios")
    void findGlobalesByIdsShouldMapRubricaWithItsCriterios() {
        givenGlobalRubricas(List.of(rubricaEntity(RUBRICA_ID)));
        givenCriterios(List.of(RUBRICA_ID), List.of(criterioEntity(RUBRICA_ID)));

        Map<UUID, Rubrica> result = findGlobalesByIds(IDS);

        thenRubricaHasCriterio(result);
    }

    @Test
    @DisplayName("Devuelve criterios vacíos para la rúbrica que no tiene criterios")
    void findGlobalesByIdsShouldReturnEmptyCriteriosWhenRubricaHasNone() {
        givenGlobalRubricas(List.of(rubricaEntity(RUBRICA_ID), rubricaEntity(OTRA_RUBRICA_ID)));
        givenCriterios(IDS, List.of(criterioEntity(RUBRICA_ID)));

        Map<UUID, Rubrica> result = findGlobalesByIds(IDS);

        assertThat(result).containsOnlyKeys(RUBRICA_ID, OTRA_RUBRICA_ID);
        assertThat(result.get(OTRA_RUBRICA_ID).criterios()).isEmpty();
    }

    @Test
    @DisplayName("Consulta los criterios de todas las rúbricas globales en una sola consulta")
    void findGlobalesByIdsShouldQueryCriteriosOnceForAllRubricas() {
        givenGlobalRubricas(List.of(rubricaEntity(RUBRICA_ID), rubricaEntity(OTRA_RUBRICA_ID)));
        givenCriterios(IDS, List.of(criterioEntity(RUBRICA_ID)));

        findGlobalesByIds(IDS);

        verify(criterios, times(1)).findGlobalesByRubricaIds(IDS);
    }

    // --- arrange ---
    private void givenGlobalRubricas(List<RubricaEntity> entities) {
        when(rubricas.findGlobalesByIds(IDS)).thenReturn(entities);
    }

    private void givenCriterios(List<UUID> rubricaIds, List<RubricaCriterioEntity> entities) {
        when(criterios.findGlobalesByRubricaIds(rubricaIds)).thenReturn(entities);
    }

    // --- helpers ---
    private RubricaEntity rubricaEntity(UUID id) {
        return RubricaEntity.builder()
                .id(id)
                .nombre("Escritura")
                .descripcion("Rúbrica base")
                .puntajeAprobacion(new BigDecimal("2.25"))
                .build();
    }

    private RubricaCriterioEntity criterioEntity(UUID rubricaId) {
        return RubricaCriterioEntity.builder()
                .id(CRITERIO_ID)
                .rubricaId(rubricaId)
                .posicion((short) 1)
                .nombre("Claridad")
                .descripcion("Detalle")
                .puntajeMax((short) 3)
                .peso(new BigDecimal("0.50"))
                .eliminatorio(true)
                .build();
    }

    // --- act ---
    private Map<UUID, Rubrica> findGlobalesByIds(List<UUID> ids) {
        return rubricaRepositoryImpl.findGlobalesByIds(ids);
    }

    // --- assert ---
    private void thenResultIsEmptyAndNothingQueried(Map<UUID, Rubrica> result) {
        assertThat(result).isEmpty();
        verifyNoInteractions(rubricas, criterios);
    }

    private void thenRubricaHasCriterio(Map<UUID, Rubrica> result) {
        assertThat(result).containsOnlyKeys(RUBRICA_ID);
        Rubrica rubrica = result.get(RUBRICA_ID);
        assertThat(rubrica.id()).isEqualTo(RUBRICA_ID);
        assertThat(rubrica.nombre()).isEqualTo("Escritura");
        assertThat(rubrica.descripcion()).isEqualTo("Rúbrica base");
        assertThat(rubrica.puntajeAprobacion()).isEqualByComparingTo("2.25");
        assertThat(rubrica.criterios()).hasSize(1);
        CriterioRubrica criterio = rubrica.criterios().getFirst();
        assertThat(criterio.id()).isEqualTo(CRITERIO_ID);
        assertThat(criterio.nombre()).isEqualTo("Claridad");
    }
}
