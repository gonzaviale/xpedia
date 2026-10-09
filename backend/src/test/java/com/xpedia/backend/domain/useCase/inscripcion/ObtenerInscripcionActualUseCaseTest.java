package com.xpedia.backend.domain.useCase.inscripcion;

import com.xpedia.backend.domain.dto.inscripcion.InscripcionItem;
import com.xpedia.backend.domain.dto.inscripcion.ObtenerInscripcionActualRequest;
import com.xpedia.backend.domain.mapper.inscripcion.ObtenerInscripcionActualMapper;
import com.xpedia.backend.domain.service.inscripcion.InscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.NODO_ID;
import static com.xpedia.backend.support.InscripcionTestData.USUARIO_ID;
import static com.xpedia.backend.support.InscripcionTestData.recorrido;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerInscripcionActualUseCaseTest {

    @Mock
    private InscripcionService inscripcionService;

    private ObtenerInscripcionActualUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerInscripcionActualUseCase(inscripcionService, new ObtenerInscripcionActualMapper());
    }

    @Test
    @DisplayName("Delega al servicio y devuelve la inscripción mapeada")
    void executeShouldDelegateAndMapEnrollment() {
        givenServiceReturnsEnrollment();

        InscripcionItem response = execute();

        thenExecuteShouldDelegateAndMapEnrollment(response);
    }

    @Test
    @DisplayName("Propaga el error de negocio del servicio")
    void executeShouldPropagateServiceFailure() {
        givenExecuteShouldPropagateServiceFailure();

        thenExecuteShouldPropagateServiceFailure();
    }

    // --- arrange ---
    private void givenServiceReturnsEnrollment() {
        when(inscripcionService.obtenerActual(USUARIO_ID)).thenReturn(recorrido());
    }

    private void givenExecuteShouldPropagateServiceFailure() {
        when(inscripcionService.obtenerActual(USUARIO_ID)).thenThrow(new IllegalStateException("fallo"));
    }

    // --- act ---
    private InscripcionItem execute() {
        return useCase.execute(new ObtenerInscripcionActualRequest(USUARIO_ID));
    }

    // --- assert ---
    private void thenExecuteShouldDelegateAndMapEnrollment(InscripcionItem response) {
        assertThat(response.id()).isEqualTo(INSCRIPCION_ID);
        assertThat(response.progreso().getFirst().nodoId()).isEqualTo(NODO_ID);
        verify(inscripcionService).obtenerActual(USUARIO_ID);
    }

    private void thenExecuteShouldPropagateServiceFailure() {
        assertThatThrownBy(this::execute).isInstanceOf(IllegalStateException.class);
    }
}

