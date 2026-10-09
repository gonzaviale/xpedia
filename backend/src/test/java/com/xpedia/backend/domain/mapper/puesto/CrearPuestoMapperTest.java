package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.CrearPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoResponse;
import com.xpedia.backend.domain.model.puesto.Puesto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CrearPuestoMapperTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Chat N1";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-10T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-02-11T11:30:00Z");

    private final CrearPuestoMapper mapper = new CrearPuestoMapper();

    @Test
    @DisplayName("toModel arma un puesto nuevo sin id con la organización y el nombre del request")
    void toModelShouldBuildNewPuestoWithoutId() {
        Puesto model = toModel();

        thenModelIsNewPuesto(model);
    }

    @Test
    @DisplayName("toResponse copia todos los campos del puesto")
    void toResponseShouldMapEveryPuestoField() {
        CrearPuestoResponse response = toResponse(puesto());

        thenResponseHasEveryField(response);
    }

    // --- helpers ---
    private Puesto puesto() {
        return Puesto.builder()
                .id(PUESTO_ID)
                .organizacionId(ORGANIZACION_ID)
                .nombre(NOMBRE)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    // --- act ---
    private Puesto toModel() {
        return mapper.toModel(new CrearPuestoRequest(ORGANIZACION_ID, NOMBRE));
    }

    private CrearPuestoResponse toResponse(Puesto puesto) {
        return mapper.toResponse(puesto);
    }

    // --- assert ---
    private void thenModelIsNewPuesto(Puesto model) {
        assertThat(model.getId()).isNull();
        assertThat(model.getOrganizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(model.getNombre()).isEqualTo(NOMBRE);
        assertThat(model.getCreadoEn()).isNull();
        assertThat(model.getActualizadoEn()).isNull();
    }

    private void thenResponseHasEveryField(CrearPuestoResponse response) {
        assertThat(response.id()).isEqualTo(PUESTO_ID);
        assertThat(response.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(response.nombre()).isEqualTo(NOMBRE);
        assertThat(response.creadoEn()).isEqualTo(CREADO_EN);
        assertThat(response.actualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }
}
