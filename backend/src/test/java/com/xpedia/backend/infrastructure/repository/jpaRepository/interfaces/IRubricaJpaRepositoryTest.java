package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.RubricaEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Sql({"/db/rutas-test.sql", "/db/retos-test.sql"})
class IRubricaJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUBRICA_PONDERADA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000008");
    private static final UUID RUBRICA_PRIVADA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000002");

    @Autowired
    private IRubricaJpaRepository repository;

    @Test
    @DisplayName("Excluye las rúbricas privadas del lote")
    void findGlobalesByIdsShouldExcludePrivateRubricas() {
        List<RubricaEntity> result = findGlobalesByIds(RUBRICA_PONDERADA_ID, RUBRICA_PRIVADA_ID);

        assertThat(result).extracting(RubricaEntity::getId).containsExactly(RUBRICA_PONDERADA_ID);
    }

    @Test
    @DisplayName("Conserva el puntaje decimal y la descripción nula de la rúbrica")
    void findGlobalesByIdsShouldKeepDecimalThresholdAndNullDescripcion() {
        List<RubricaEntity> result = findGlobalesByIds(RUBRICA_PONDERADA_ID);

        assertThat(result.getFirst().getPuntajeAprobacion()).isEqualByComparingTo("2.25");
        assertThat(result.getFirst().getDescripcion()).isNull();
    }

    @Test
    @DisplayName("Devuelve lista vacía cuando el id no existe")
    void findGlobalesByIdsShouldReturnEmptyWhenIdIsAbsent() {
        List<RubricaEntity> result = findGlobalesByIds(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    // --- act ---
    private List<RubricaEntity> findGlobalesByIds(UUID... ids) {
        return repository.findGlobalesByIds(List.of(ids));
    }
}
