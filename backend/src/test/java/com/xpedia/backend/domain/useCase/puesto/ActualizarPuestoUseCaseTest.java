package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoResponse;
import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.mapper.puesto.ActualizarPuestoMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarPuestoUseCaseTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Voz N2";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-10T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-02-11T11:30:00Z");

    @Mock
    private PuestoService puestoService;

    private ActualizarPuestoUseCase actualizarPuestoUseCase;

    @BeforeEach
    void setUp() {
        actualizarPuestoUseCase = new ActualizarPuestoUseCase(puestoService, new ActualizarPuestoMapper());
    }

    @Test
    @DisplayName("Delega al servicio el id del request y un puesto que solo lleva el nombre")
    void executeShouldDelegateIdAndNombreOnlyPuestoToService() {
        givenServiceUpdatesPuesto();

        actualizar();

        thenServiceReceivedIdAndNombreOnlyPuesto();
    }

    @Test
    @DisplayName("Devuelve el puesto actualizado con todos sus campos")
    void executeShouldReturnUpdatedPuesto() {
        givenServiceUpdatesPuesto();

        ActualizarPuestoResponse response = actualizar();

        thenResponseHasUpdatedPuesto(response);
    }

    @Test
    @DisplayName("Propaga el error de duplicado del servicio")
    void executeShouldPropagateDuplicateErrorFromService() {
        DuplicateResourceException error = givenServiceThrowsDuplicate();

        assertThatThrownBy(this::actualizar).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceUpdatesPuesto() {
        when(puestoService.actualizar(eq(PUESTO_ID), any(Puesto.class))).thenReturn(updatedPuesto());
    }

    private DuplicateResourceException givenServiceThrowsDuplicate() {
        DuplicateResourceException error = new DuplicateResourceException("Duplicado");
        when(puestoService.actualizar(eq(PUESTO_ID), any(Puesto.class))).thenThrow(error);
        return error;
    }

    // --- helpers ---
    private Puesto updatedPuesto() {
        return Puesto.builder()
                .id(PUESTO_ID)
                .organizacionId(ORGANIZACION_ID)
                .nombre(NOMBRE)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    // --- act ---
    private ActualizarPuestoResponse actualizar() {
        return actualizarPuestoUseCase.execute(new ActualizarPuestoRequest(PUESTO_ID, NOMBRE));
    }

    // --- assert ---
    private void thenServiceReceivedIdAndNombreOnlyPuesto() {
        ArgumentCaptor<Puesto> captor = ArgumentCaptor.forClass(Puesto.class);
        verify(puestoService).actualizar(eq(PUESTO_ID), captor.capture());
        Puesto changes = captor.getValue();
        assertThat(changes.getId()).isNull();
        assertThat(changes.getOrganizacionId()).isNull();
        assertThat(changes.getNombre()).isEqualTo(NOMBRE);
    }

    private void thenResponseHasUpdatedPuesto(ActualizarPuestoResponse response) {
        assertThat(response.id()).isEqualTo(PUESTO_ID);
        assertThat(response.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(response.nombre()).isEqualTo(NOMBRE);
        assertThat(response.creadoEn()).isEqualTo(CREADO_EN);
        assertThat(response.actualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }
}
