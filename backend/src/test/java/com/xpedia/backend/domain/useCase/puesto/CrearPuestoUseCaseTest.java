package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.CrearPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoResponse;
import com.xpedia.backend.domain.mapper.puesto.CrearPuestoMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearPuestoUseCaseTest {

    private static final UUID GENERATED_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Chat N1";

    @Mock
    private PuestoService puestoService;

    private CrearPuestoUseCase crearPuestoUseCase;

    @BeforeEach
    void setUp() {
        crearPuestoUseCase = new CrearPuestoUseCase(puestoService, new CrearPuestoMapper());
    }

    @Test
    @DisplayName("Construye un puesto sin id con la organización y el nombre, y lo delega al servicio")
    void executeShouldBuildPuestoWithoutIdAndDelegateToService() {
        givenServiceCreatesPuesto();

        crear();

        thenServiceReceivedNewPuesto();
    }

    @Test
    @DisplayName("Devuelve el puesto creado con el id generado")
    void executeShouldReturnCreatedPuesto() {
        givenServiceCreatesPuesto();

        CrearPuestoResponse response = crear();

        thenResponseHasCreatedPuesto(response);
    }

    // --- arrange ---
    private void givenServiceCreatesPuesto() {
        when(puestoService.crear(any(Puesto.class))).thenReturn(savedPuesto());
    }

    private Puesto savedPuesto() {
        return Puesto.builder()
                .id(GENERATED_ID)
                .organizacionId(ORGANIZACION_ID)
                .nombre(NOMBRE)
                .build();
    }

    // --- act ---
    private CrearPuestoResponse crear() {
        return crearPuestoUseCase.execute(new CrearPuestoRequest(ORGANIZACION_ID, NOMBRE));
    }

    // --- assert ---
    private void thenServiceReceivedNewPuesto() {
        ArgumentCaptor<Puesto> captor = ArgumentCaptor.forClass(Puesto.class);
        verify(puestoService).crear(captor.capture());
        Puesto captured = captor.getValue();
        assertThat(captured.getId()).isNull();
        assertThat(captured.getOrganizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(captured.getNombre()).isEqualTo(NOMBRE);
    }

    private void thenResponseHasCreatedPuesto(CrearPuestoResponse response) {
        assertThat(response.id()).isEqualTo(GENERATED_ID);
        assertThat(response.organizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(response.nombre()).isEqualTo(NOMBRE);
    }
}
