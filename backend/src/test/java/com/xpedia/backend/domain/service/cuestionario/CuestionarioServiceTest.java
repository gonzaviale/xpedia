package com.xpedia.backend.domain.service.cuestionario;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.repository.cuestionario.CuestionarioRepository;
import com.xpedia.backend.domain.service.nodo.NodoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuestionarioServiceTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();

    @Mock
    private NodoService nodoService;

    @Mock
    private CuestionarioRepository cuestionarioRepository;

    @InjectMocks
    private CuestionarioService cuestionarioService;

    @Test
    @DisplayName("Lista los cuestionarios aprobados del nodo")
    void listarShouldReturnCuestionariosOfNodo() {
        Cuestionario cuestionario = cuestionario();
        givenRepositoryListsCuestionarios(List.of(cuestionario));

        List<Cuestionario> result = listar();

        assertThat(result).containsExactly(cuestionario);
    }

    @Test
    @DisplayName("Valida la visibilidad del nodo antes de consultar los cuestionarios")
    void listarShouldValidateNodoBeforeQueryingCuestionarios() {
        givenRepositoryListsCuestionarios(List.of());

        listar();

        thenNodoWasValidatedBeforeQuerying();
    }

    @Test
    @DisplayName("No consulta cuestionarios cuando el nodo no es visible")
    void listarShouldNotQueryCuestionariosWhenNodoIsNotVisible() {
        givenNodoIsNotVisible();

        assertThatThrownBy(this::listar).isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(cuestionarioRepository);
    }

    @Test
    @DisplayName("Devuelve el cuestionario aprobado cuando existe y su nodo es visible")
    void obtenerAprobadoShouldReturnCuestionarioWhenItExistsAndNodoIsVisible() {
        Cuestionario cuestionario = cuestionario();
        givenRepositoryFindsCuestionario(Optional.of(cuestionario));

        Cuestionario result = obtenerAprobado();

        assertThat(result).isSameAs(cuestionario);
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el cuestionario no existe o no está aprobado")
    void obtenerAprobadoShouldThrowWhenCuestionarioDoesNotExist() {
        givenRepositoryFindsCuestionario(Optional.empty());

        assertThatThrownBy(this::obtenerAprobado).isInstanceOf(ResourceNotFoundException.class);

        verifyNoInteractions(nodoService);
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el nodo del cuestionario no es visible")
    void obtenerAprobadoShouldThrowWhenNodoOfCuestionarioIsNotVisible() {
        givenRepositoryFindsCuestionario(Optional.of(cuestionario()));
        givenNodoIsNotVisible();

        assertThatThrownBy(this::obtenerAprobado).isInstanceOf(ResourceNotFoundException.class);
    }

    // --- arrange ---
    private void givenRepositoryListsCuestionarios(List<Cuestionario> cuestionarios) {
        when(cuestionarioRepository.findAprobadosByNodo(RUTA_ID, NODO_ID)).thenReturn(cuestionarios);
    }

    private void givenRepositoryFindsCuestionario(Optional<Cuestionario> cuestionario) {
        when(cuestionarioRepository.findAprobadoById(ACTIVIDAD_ID)).thenReturn(cuestionario);
    }

    private void givenNodoIsNotVisible() {
        doThrow(new ResourceNotFoundException("nodo", "id", NODO_ID))
                .when(nodoService).validarVisible(RUTA_ID, NODO_ID);
    }

    // --- helpers ---
    private Cuestionario cuestionario() {
        return Cuestionario.builder()
                .id(ACTIVIDAD_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .build();
    }

    // --- act ---
    private List<Cuestionario> listar() {
        return cuestionarioService.listar(RUTA_ID, NODO_ID);
    }

    private Cuestionario obtenerAprobado() {
        return cuestionarioService.obtenerAprobado(ACTIVIDAD_ID);
    }

    // --- assert ---
    private void thenNodoWasValidatedBeforeQuerying() {
        InOrder orden = inOrder(nodoService, cuestionarioRepository);
        orden.verify(nodoService).validarVisible(RUTA_ID, NODO_ID);
        orden.verify(cuestionarioRepository).findAprobadosByNodo(RUTA_ID, NODO_ID);
    }
}
