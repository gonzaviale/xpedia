package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.inscripcion.Inscripcion;
import com.xpedia.backend.infrastructure.repository.entity.InscripcionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.InscripcionTestData.inscripcion;
import static org.assertj.core.api.Assertions.assertThat;

class InscripcionRepositoryMapperTest {

    private final InscripcionRepositoryMapper mapper = new InscripcionRepositoryMapper();

    @Test
    @DisplayName("Mapea todos los campos del dominio hacia persistencia")
    void toEntityShouldMapEveryField() {
        Inscripcion model = inscripcion();

        InscripcionEntity entity = mapper.toEntity(model);

        thenFieldsMatch(entity, model);
    }

    @Test
    @DisplayName("Reconstruye todos los campos de persistencia hacia dominio")
    void toDomainShouldMapEveryField() {
        Inscripcion model = inscripcion();
        InscripcionEntity entity = mapper.toEntity(model);

        Inscripcion result = mapper.toDomain(entity);

        thenFieldsMatch(entity, result);
    }

    @Test
    @DisplayName("Conserva campos opcionales ausentes")
    void toDomainShouldKeepNullFields() {
        Inscripcion result = mapper.toDomain(new InscripcionEntity());

        thenFieldsAreNull(result);
    }

    // --- assert ---
    private void thenFieldsMatch(InscripcionEntity entity, Inscripcion model) {
        assertThat(entity.getId()).isEqualTo(model.getId());
        assertThat(entity.getUsuarioId()).isEqualTo(model.getUsuarioId());
        assertThat(entity.getRutaId()).isEqualTo(model.getRutaId());
        assertThat(entity.getObjetivo()).isEqualTo(model.getObjetivo());
        assertThat(entity.getMetaPersonal()).isEqualTo(model.getMetaPersonal());
        assertThat(entity.getRitmoMin()).isEqualTo(model.getRitmoMin());
        assertThat(entity.getFechaLlegadaEstimada()).isEqualTo(model.getFechaLlegadaEstimada());
        assertThat(entity.getEstado()).isEqualTo(model.getEstado());
        assertThat(entity.getHitoActualId()).isEqualTo(model.getHitoActualId());
        assertThat(entity.getIniciadaEn()).isEqualTo(model.getIniciadaEn());
        assertThat(entity.getCreadoEn()).isEqualTo(model.getCreadoEn());
        assertThat(entity.getActualizadoEn()).isEqualTo(model.getActualizadoEn());
    }

    private void thenFieldsAreNull(Inscripcion model) {
        assertThat(model.getId()).isNull();
        assertThat(model.getUsuarioId()).isNull();
        assertThat(model.getRutaId()).isNull();
        assertThat(model.getObjetivo()).isNull();
        assertThat(model.getMetaPersonal()).isNull();
        assertThat(model.getRitmoMin()).isNull();
        assertThat(model.getFechaLlegadaEstimada()).isNull();
        assertThat(model.getEstado()).isNull();
        assertThat(model.getHitoActualId()).isNull();
        assertThat(model.getIniciadaEn()).isNull();
        assertThat(model.getCreadoEn()).isNull();
        assertThat(model.getActualizadoEn()).isNull();
    }
}

