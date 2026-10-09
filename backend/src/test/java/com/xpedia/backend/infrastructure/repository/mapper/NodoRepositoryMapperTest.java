package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.infrastructure.repository.entity.NodoEntity;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class NodoRepositoryMapperTest {
    @Test void conservaCamposYUsaSoloReferenciasValidadas() {
        var entity = full(NodoEntity.class); var rama = UUID.randomUUID(); var habilidad = UUID.randomUUID(); var prev = List.of(UUID.randomUUID());
        var result = new NodoRepositoryMapper().toDomain(entity, prev, rama, habilidad);
        assertThat(result).usingRecursiveComparison().ignoringFields("palabrasClave", "prerrequisitoIds", "ramaId", "habilidadId").isEqualTo(entity);
        assertThat(result.getPalabrasClave()).containsExactly("comunicación", "cliente");
        assertThat(result.getPrerrequisitoIds()).isEqualTo(prev); assertThat(result.getRamaId()).isEqualTo(rama); assertThat(result.getHabilidadId()).isEqualTo(habilidad);
    }
    @Test void referenciasOcultasNoSeRecuperanDelEntityYArraysVaciosSeConservan() {
        var entity = full(NodoEntity.class); entity.setPalabrasClave(new String[0]);
        var result = new NodoRepositoryMapper().toDomain(entity, List.of(), null, null);
        assertThat(result.getRamaId()).isNull(); assertThat(result.getHabilidadId()).isNull();
        assertThat(result.getPalabrasClave()).isEmpty(); assertThat(result.getPrerrequisitoIds()).isEmpty();
    }
}
