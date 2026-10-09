package com.xpedia.backend.domain.useCase.cuestionario;

import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosRequest;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosResponse;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.cuestionario.ListarCuestionariosMapper;
import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.service.cuestionario.CuestionarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCuestionariosUseCaseTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID CUESTIONARIO_ID = UUID.randomUUID();
    private static final String TITULO = "Escucha activa";

    @Mock
    private CuestionarioService cuestionarioService;

    private ListarCuestionariosUseCase listarCuestionariosUseCase;

    @BeforeEach
    void setUp() {
        listarCuestionariosUseCase = new ListarCuestionariosUseCase(
                cuestionarioService,
                new ListarCuestionariosMapper());
    }

    @Test
    @DisplayName("Lista los cuestionarios del nodo pedido y los devuelve mapeados")
    void executeShouldReturnMappedCuestionariosOfNodo() {
        givenServiceListsCuestionarios(List.of(cuestionario()));

        ListarCuestionariosResponse response = listar();

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().id()).isEqualTo(CUESTIONARIO_ID);
        assertThat(response.content().getFirst().titulo()).isEqualTo(TITULO);
    }

    @Test
    @DisplayName("Delega la consulta al servicio con el id de la ruta y del nodo")
    void executeShouldDelegateToServiceWithRutaIdAndNodoId() {
        givenServiceListsCuestionarios(List.of());

        listar();

        verify(cuestionarioService).listar(RUTA_ID, NODO_ID);
    }

    @Test
    @DisplayName("Propaga el error de dominio del servicio sin convertirlo")
    void executeShouldPropagateDomainErrorWhenServiceFails() {
        ResourceNotFoundException error = new ResourceNotFoundException("nodo", "id", NODO_ID);
        when(cuestionarioService.listar(RUTA_ID, NODO_ID)).thenThrow(error);

        assertThatThrownBy(this::listar).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceListsCuestionarios(List<Cuestionario> cuestionarios) {
        when(cuestionarioService.listar(RUTA_ID, NODO_ID)).thenReturn(cuestionarios);
    }

    // --- helpers ---
    private Cuestionario cuestionario() {
        return Cuestionario.builder()
                .id(CUESTIONARIO_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .titulo(TITULO)
                .preguntas(List.of())
                .build();
    }

    // --- act ---
    private ListarCuestionariosResponse listar() {
        return listarCuestionariosUseCase.execute(new ListarCuestionariosRequest(RUTA_ID, NODO_ID));
    }
}
