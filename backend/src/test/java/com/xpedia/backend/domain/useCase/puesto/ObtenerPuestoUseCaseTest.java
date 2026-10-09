package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoResponse;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.puesto.ObtenerPuestoMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerPuestoUseCaseTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Chat N1";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-10T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-02-11T11:30:00Z");

    @Mock
    private PuestoService puestoService;

    private ObtenerPuestoUseCase obtenerPuestoUseCase;

    @BeforeEach
    void setUp() {
        obtenerPuestoUseCase = new ObtenerPuestoUseCase(puestoService, new ObtenerPuestoMapper());
    }

    @Test
    @DisplayName("Devuelve el puesto obtenido del servicio con todos sus campos")
    void executeShouldReturnPuestoFromService() {
        givenServiceReturnsPuesto();

        ObtenerPuestoResponse response = obtener();

        thenResponseHasPuesto(response);
    }

    @Test
    @DisplayName("Propaga el error cuando el puesto no existe")
    void executeShouldPropagateNotFoundErrorFromService() {
        ResourceNotFoundException error = givenServiceThrowsNotFound();

        assertThatThrownBy(this::obtener).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceReturnsPuesto() {
        when(puestoService.obtener(PUESTO_ID)).thenReturn(savedPuesto());
    }

    private ResourceNotFoundException givenServiceThrowsNotFound() {
        ResourceNotFoundException error = new ResourceNotFoundException("Ausente");
        when(puestoService.obtener(PUESTO_ID)).thenThrow(error);
        return error;
    }

    // --- helpers ---
    private Puesto savedPuesto() {
        return Puesto.builder()
                .id(PUESTO_ID)
                .organizacionId(ORGANIZACION_ID)
                .nombre(NOMBRE)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    // --- act ---
    private ObtenerPuestoResponse obtener() {
        return obtenerPuestoUseCase.execute(new ObtenerPuestoRequest(PUESTO_ID));
    }

    // --- assert ---
    private void thenResponseHasPuesto(ObtenerPuestoResponse response) {
        assertThat(response.id()).isEqualTo(PUESTO_ID);
        assertThat(response.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(response.nombre()).isEqualTo(NOMBRE);
        assertThat(response.creadoEn()).isEqualTo(CREADO_EN);
        assertThat(response.actualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }
}
