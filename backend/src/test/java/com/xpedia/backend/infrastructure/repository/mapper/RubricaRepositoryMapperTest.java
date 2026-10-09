package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.rubrica.CriterioRubrica;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import com.xpedia.backend.infrastructure.repository.entity.RubricaCriterioEntity;
import com.xpedia.backend.infrastructure.repository.entity.RubricaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RubricaRepositoryMapperTest {

    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");

    private RubricaRepositoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RubricaRepositoryMapper();
    }

    @Test
    @DisplayName("Mapea cada campo de la rúbrica con los criterios recibidos")
    void toDomainShouldMapEveryRubricaFieldWithGivenCriterios() {
        CriterioRubrica criterio = criterio();

        Rubrica rubrica = toDomain(rubricaEntity("Rúbrica base"), List.of(criterio));

        thenRubricaHasFields(rubrica, "Rúbrica base");
        assertThat(rubrica.criterios()).containsExactly(criterio);
    }

    @Test
    @DisplayName("Conserva la descripción nula de la rúbrica")
    void toDomainShouldKeepNullDescripcion() {
        Rubrica rubrica = toDomain(rubricaEntity(null), List.of());

        assertThat(rubrica.descripcion()).isNull();
    }

    @Test
    @DisplayName("Mapea cada campo del criterio")
    void toCriterioShouldMapEveryCriterioField() {
        CriterioRubrica criterio = toCriterio(criterioEntity("Detalle"));

        thenCriterioHasFields(criterio, "Detalle");
    }

    @Test
    @DisplayName("Conserva la descripción nula del criterio")
    void toCriterioShouldKeepNullDescripcion() {
        CriterioRubrica criterio = toCriterio(criterioEntity(null));

        assertThat(criterio.descripcion()).isNull();
    }

    // --- helpers ---
    private RubricaEntity rubricaEntity(String descripcion) {
        return RubricaEntity.builder()
                .id(RUBRICA_ID)
                .nombre("Escritura")
                .descripcion(descripcion)
                .puntajeAprobacion(new BigDecimal("2.25"))
                .build();
    }

    private RubricaCriterioEntity criterioEntity(String descripcion) {
        return RubricaCriterioEntity.builder()
                .id(CRITERIO_ID)
                .rubricaId(RUBRICA_ID)
                .posicion((short) 2)
                .nombre("Política")
                .descripcion(descripcion)
                .puntajeMax((short) 3)
                .peso(new BigDecimal("0.75"))
                .eliminatorio(true)
                .build();
    }

    private CriterioRubrica criterio() {
        return new CriterioRubrica(
                CRITERIO_ID, (short) 2, "Política", "Detalle", (short) 3, new BigDecimal("0.75"), true);
    }

    // --- act ---
    private Rubrica toDomain(RubricaEntity entity, List<CriterioRubrica> criterios) {
        return mapper.toDomain(entity, criterios);
    }

    private CriterioRubrica toCriterio(RubricaCriterioEntity entity) {
        return mapper.toCriterio(entity);
    }

    // --- assert ---
    private void thenRubricaHasFields(Rubrica rubrica, String descripcion) {
        assertThat(rubrica.id()).isEqualTo(RUBRICA_ID);
        assertThat(rubrica.nombre()).isEqualTo("Escritura");
        assertThat(rubrica.descripcion()).isEqualTo(descripcion);
        assertThat(rubrica.puntajeAprobacion()).isEqualByComparingTo("2.25");
    }

    private void thenCriterioHasFields(CriterioRubrica criterio, String descripcion) {
        assertThat(criterio.id()).isEqualTo(CRITERIO_ID);
        assertThat(criterio.posicion()).isEqualTo((short) 2);
        assertThat(criterio.nombre()).isEqualTo("Política");
        assertThat(criterio.descripcion()).isEqualTo(descripcion);
        assertThat(criterio.puntajeMax()).isEqualTo((short) 3);
        assertThat(criterio.peso()).isEqualByComparingTo("0.75");
        assertThat(criterio.eliminatorio()).isTrue();
    }
}
