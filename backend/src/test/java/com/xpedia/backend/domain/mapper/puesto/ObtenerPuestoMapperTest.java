package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoResponse;
import com.xpedia.backend.domain.model.puesto.Puesto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObtenerPuestoMapperTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Chat N1";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-10T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-02-11T11:30:00Z");

    private final ObtenerPuestoMapper mapper = new ObtenerPuestoMapper();

    @Test
    @DisplayName("toResponse copia todos los campos del puesto")
    void toResponseShouldMapEveryPuestoField() {
        ObtenerPuestoResponse response = toResponse(puesto());

        thenResponseHasEveryField(response);
    }

    @Test
    @DisplayName("toResponse conserva los campos nulos de un puesto vacío")
    void toResponseShouldKeepNullFieldsWhenPuestoIsEmpty() {
        ObtenerPuestoResponse response = toResponse(new Puesto());

        thenResponseHasNoFields(response);
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
    private ObtenerPuestoResponse toResponse(Puesto puesto) {
        return mapper.toResponse(puesto);
    }

    // --- assert ---
    private void thenResponseHasEveryField(ObtenerPuestoResponse response) {
        assertThat(response.id()).isEqualTo(PUESTO_ID);
        assertThat(response.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(response.nombre()).isEqualTo(NOMBRE);
        assertThat(response.creadoEn()).isEqualTo(CREADO_EN);
        assertThat(response.actualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }

    private void thenResponseHasNoFields(ObtenerPuestoResponse response) {
        assertThat(response.id()).isNull();
        assertThat(response.organizacionId()).isNull();
        assertThat(response.nombre()).isNull();
        assertThat(response.creadoEn()).isNull();
        assertThat(response.actualizadoEn()).isNull();
    }
}
