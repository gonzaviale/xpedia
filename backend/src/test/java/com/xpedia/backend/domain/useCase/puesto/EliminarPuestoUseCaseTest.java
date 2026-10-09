package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.EliminarPuestoRequest;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EliminarPuestoUseCaseTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();

    @Mock
    private PuestoService puestoService;

    private EliminarPuestoUseCase eliminarPuestoUseCase;

    @BeforeEach
    void setUp() {
        eliminarPuestoUseCase = new EliminarPuestoUseCase(puestoService);
    }

    @Test
    @DisplayName("Elimina el puesto con el id recibido en el request")
    void executeShouldDeletePuestoWithRequestedId() {
        eliminar();

        thenServiceDeletedPuesto();
    }

    @Test
    @DisplayName("Propaga el error cuando el puesto no existe")
    void executeShouldPropagateNotFoundErrorFromService() {
        ResourceNotFoundException error = givenServiceThrowsNotFound();

        assertThatThrownBy(this::eliminar).isSameAs(error);
    }

    // --- arrange ---
    private ResourceNotFoundException givenServiceThrowsNotFound() {
        ResourceNotFoundException error = new ResourceNotFoundException("Ausente");
        doThrow(error).when(puestoService).eliminar(PUESTO_ID);
        return error;
    }

    // --- act ---
    private void eliminar() {
        eliminarPuestoUseCase.execute(new EliminarPuestoRequest(PUESTO_ID));
    }

    // --- assert ---
    private void thenServiceDeletedPuesto() {
        verify(puestoService).eliminar(PUESTO_ID);
    }
}
