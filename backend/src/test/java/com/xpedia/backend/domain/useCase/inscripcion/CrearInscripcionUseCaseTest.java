package com.xpedia.backend.domain.useCase.inscripcion;

import com.xpedia.backend.domain.dto.inscripcion.CrearInscripcionRequest;
import com.xpedia.backend.domain.dto.inscripcion.InscripcionItem;
import com.xpedia.backend.domain.mapper.inscripcion.CrearInscripcionMapper;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.service.inscripcion.InscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneOffset;

import static com.xpedia.backend.support.InscripcionTestData.FECHA;
import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.META;
import static com.xpedia.backend.support.InscripcionTestData.NODO_ID;
import static com.xpedia.backend.support.InscripcionTestData.RUTA_ID;
import static com.xpedia.backend.support.InscripcionTestData.USUARIO_ID;
import static com.xpedia.backend.support.InscripcionTestData.recorrido;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearInscripcionUseCaseTest {

    @Mock
    private InscripcionService inscripcionService;

    private CrearInscripcionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CrearInscripcionUseCase(inscripcionService, new CrearInscripcionMapper(),
                Clock.fixed(FECHA.toInstant(), ZoneOffset.UTC));
    }

    @Test
    @DisplayName("Delega al servicio y devuelve la inscripción mapeada con fecha determinista")
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
        when(inscripcionService.crear(USUARIO_ID, RUTA_ID, ObjetivoRuta.CAMBIAR, META, (short) 20, FECHA))
                .thenReturn(recorrido());
    }

    private void givenExecuteShouldPropagateServiceFailure() {
        when(inscripcionService.crear(USUARIO_ID, RUTA_ID, ObjetivoRuta.CAMBIAR, META, (short) 20, FECHA))
                .thenThrow(new IllegalStateException("fallo"));
    }

    // --- act ---
    private InscripcionItem execute() {
        return useCase.execute(new CrearInscripcionRequest(
                USUARIO_ID, RUTA_ID, ObjetivoRuta.CAMBIAR, META, (short) 20));
    }

    // --- assert ---
    private void thenExecuteShouldDelegateAndMapEnrollment(InscripcionItem response) {
        assertThat(response.id()).isEqualTo(INSCRIPCION_ID);
        assertThat(response.progreso().getFirst().nodoId()).isEqualTo(NODO_ID);
        verify(inscripcionService).crear(USUARIO_ID, RUTA_ID, ObjetivoRuta.CAMBIAR, META, (short) 20, FECHA);
    }

    private void thenExecuteShouldPropagateServiceFailure() {
        assertThatThrownBy(this::execute).isInstanceOf(IllegalStateException.class);
    }
}

