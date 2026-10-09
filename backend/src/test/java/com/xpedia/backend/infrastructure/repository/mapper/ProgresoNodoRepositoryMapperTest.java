package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.inscripcion.ProgresoNodo;
import com.xpedia.backend.infrastructure.repository.entity.ProgresoNodoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.InscripcionTestData.progreso;
import static org.assertj.core.api.Assertions.assertThat;

class ProgresoNodoRepositoryMapperTest {

    private final ProgresoNodoRepositoryMapper mapper = new ProgresoNodoRepositoryMapper();

    @Test
    @DisplayName("Mapea todos los campos del dominio hacia persistencia")
    void toEntityShouldMapEveryField() {
        ProgresoNodo model = progreso();

        ProgresoNodoEntity entity = mapper.toEntity(model);

        thenFieldsMatch(entity, model);
    }

    @Test
    @DisplayName("Reconstruye todos los campos de persistencia hacia dominio")
    void toDomainShouldMapEveryField() {
        ProgresoNodo model = progreso();
        ProgresoNodoEntity entity = mapper.toEntity(model);

        ProgresoNodo result = mapper.toDomain(entity);

        thenFieldsMatch(entity, result);
    }

    @Test
    @DisplayName("Conserva campos opcionales ausentes")
    void toDomainShouldKeepNullFields() {
        ProgresoNodo result = mapper.toDomain(new ProgresoNodoEntity());

        thenFieldsAreNull(result);
    }

    // --- assert ---
    private void thenFieldsMatch(ProgresoNodoEntity entity, ProgresoNodo model) {
        assertThat(entity.getInscripcionId()).isEqualTo(model.getInscripcionId());
        assertThat(entity.getNodoId()).isEqualTo(model.getNodoId());
        assertThat(entity.getEstado()).isEqualTo(model.getEstado());
        assertThat(entity.getDominio()).isEqualTo(model.getDominio());
        assertThat(entity.getNivel()).isEqualTo(model.getNivel());
        assertThat(entity.getCantidadFallos()).isEqualTo(model.getCantidadFallos());
        assertThat(entity.getCreadoEn()).isEqualTo(model.getCreadoEn());
        assertThat(entity.getActualizadoEn()).isEqualTo(model.getActualizadoEn());
    }

    private void thenFieldsAreNull(ProgresoNodo model) {
        assertThat(model.getInscripcionId()).isNull();
        assertThat(model.getNodoId()).isNull();
        assertThat(model.getEstado()).isNull();
        assertThat(model.getDominio()).isNull();
        assertThat(model.getNivel()).isNull();
        assertThat(model.getCantidadFallos()).isNull();
        assertThat(model.getCreadoEn()).isNull();
        assertThat(model.getActualizadoEn()).isNull();
    }
}

