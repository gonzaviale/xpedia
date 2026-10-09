package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.RubricaCriterioEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/db/rutas-test.sql", "/db/retos-test.sql"})
class IRubricaCriterioJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUBRICA_GLOBAL_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID RUBRICA_PRIVADA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000002");
    private static final UUID RUBRICA_SIN_CRITERIOS_ID = UUID.fromString("d1000000-0000-4000-8000-000000000003");

    @Autowired
    private IRubricaCriterioJpaRepository repository;

    @Test
    @DisplayName("Ordena los criterios por posición aunque se hayan insertado invertidos")
    void findGlobalesByRubricaIdsShouldOrderCriteriosByPosition() {
        List<RubricaCriterioEntity> result = findGlobalesByRubricaIds(RUBRICA_GLOBAL_ID);

        assertThat(result).extracting(RubricaCriterioEntity::getNombre)
                .containsExactly("Claridad", "Empatía", "Próximo paso", "Política");
    }

    @Test
    @DisplayName("Oculta los criterios de rúbricas privadas")
    void findGlobalesByRubricaIdsShouldHideCriteriosOfPrivateRubricas() {
        List<RubricaCriterioEntity> result = findGlobalesByRubricaIds(RUBRICA_GLOBAL_ID, RUBRICA_PRIVADA_ID);

        assertThat(result).extracting(RubricaCriterioEntity::getNombre)
                .doesNotContain("Criterio");
    }

    @Test
    @DisplayName("Conserva los campos del criterio, incluyendo peso, eliminatorio y descripción nula")
    void findGlobalesByRubricaIdsShouldKeepCriterioFields() {
        List<RubricaCriterioEntity> result = findGlobalesByRubricaIds(RUBRICA_GLOBAL_ID);

        assertThat(result.getLast().isEliminatorio()).isTrue();
        assertThat(result.getFirst().getPeso()).isEqualByComparingTo("1");
        assertThat(result.getFirst().getDescripcion()).isNull();
    }

    @Test
    @DisplayName("Devuelve lista vacía para una rúbrica sin criterios")
    void findGlobalesByRubricaIdsShouldReturnEmptyWhenRubricaHasNoCriterios() {
        List<RubricaCriterioEntity> result = findGlobalesByRubricaIds(RUBRICA_SIN_CRITERIOS_ID);

        assertThat(result).isEmpty();
    }

    // --- act ---
    private List<RubricaCriterioEntity> findGlobalesByRubricaIds(UUID... ids) {
        return repository.findGlobalesByRubricaIds(List.of(ids));
    }
}
