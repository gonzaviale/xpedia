package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.NodoPrerrequisitoEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class INodoPrerrequisitoJpaRepositoryTest extends PostgresRepositoryTestSupport {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_CON_PRERREQUISITOS_ID = UUID.fromString("00000000-0000-0000-0000-000000000503");
    private static final UUID NODO_SIN_PRERREQUISITOS_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID PRIMER_PRERREQUISITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID SEGUNDO_PRERREQUISITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000502");

    @Autowired
    private INodoPrerrequisitoJpaRepository repository;

    @Test
    @DisplayName("Conserva los prerrequisitos válidos de otro hito y excluye las relaciones ajenas")
    void findDeNodosEnRutaShouldKeepValidPrerrequisitosAndExcludeForeignRelations() {
        List<NodoPrerrequisitoEntity> result = findPrerrequisitos(NODO_CON_PRERREQUISITOS_ID);

        thenPrerrequisitosAre(result, PRIMER_PRERREQUISITO_ID, SEGUNDO_PRERREQUISITO_ID);
    }

    @Test
    @DisplayName("Devuelve lista vacía cuando el nodo no tiene prerrequisitos")
    void findDeNodosEnRutaShouldReturnEmptyListWhenNodoHasNoPrerrequisitos() {
        List<NodoPrerrequisitoEntity> result = findPrerrequisitos(NODO_SIN_PRERREQUISITOS_ID);

        assertThat(result).isEmpty();
    }

    // --- act ---
    private List<NodoPrerrequisitoEntity> findPrerrequisitos(UUID nodoId) {
        return repository.findDeNodosEnRuta(List.of(nodoId), RUTA_ID);
    }

    // --- assert ---
    private void thenPrerrequisitosAre(List<NodoPrerrequisitoEntity> result, UUID... prerrequisitoIds) {
        assertThat(result).extracting(NodoPrerrequisitoEntity::getPrerrequisitoId)
                .containsExactly(prerrequisitoIds);
    }
}
