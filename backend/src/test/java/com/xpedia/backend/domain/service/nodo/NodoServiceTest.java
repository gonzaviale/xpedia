package com.xpedia.backend.domain.service.nodo;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.domain.service.hito.HitoService;
import com.xpedia.backend.domain.service.ruta.RutaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodoServiceTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID HITO_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();

    @Mock
    private NodoRepository nodoRepository;

    @Mock
    private RutaService rutaService;

    @Mock
    private HitoService hitoService;

    @InjectMocks
    private NodoService nodoService;

    @Test
    @DisplayName("No valida el hito y devuelve los nodos cuando no se pasa hito")
    void listarShouldReturnNodosWithoutValidatingHitoWhenHitoIsNull() {
        List<Nodo> nodos = List.of(nodo());
        givenRepositoryHasNodos(null, nodos);

        List<Nodo> result = listar(null);

        thenResultIsAndHitoWasNotValidated(result, nodos);
    }

    @Test
    @DisplayName("Valida la ruta y la pertenencia del hito antes de consultar cuando se pasa hito")
    void listarShouldValidateRutaAndHitoBeforeQueryingWhenHitoIsGiven() {
        givenRepositoryHasNodos(HITO_ID, List.of());

        listar(HITO_ID);

        thenRutaAndHitoWereValidatedBeforeQuery();
    }

    @Test
    @DisplayName("No consulta nodos ni valida hito cuando la ruta no es visible")
    void listarShouldNotQueryNorValidateHitoWhenRutaIsNotVisible() {
        givenRutaIsNotVisible();

        assertThatThrownBy(() -> listar(null)).isInstanceOf(ResourceNotFoundException.class);

        thenNothingElseWasCalled();
    }

    @Test
    @DisplayName("No consulta nodos cuando el hito no pertenece a la ruta")
    void listarShouldNotQueryNodosWhenHitoDoesNotBelongToRuta() {
        givenHitoDoesNotBelongToRuta();

        assertThatThrownBy(() -> listar(HITO_ID)).isInstanceOf(ResourceNotFoundException.class);

        thenRepositoryWasNotQueried();
    }

    @Test
    @DisplayName("Valida la visibilidad de la ruta y acepta el nodo cuando es visible")
    void validarVisibleShouldPassWhenRutaAndNodoAreVisible() {
        givenNodoIsVisible(true);

        assertThatCode(this::validarVisible).doesNotThrowAnyException();

        thenRutaWasValidated();
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el nodo no es visible")
    void validarVisibleShouldThrowWhenNodoIsNotVisible() {
        givenNodoIsVisible(false);

        assertThatThrownBy(this::validarVisible).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("No consulta el nodo cuando la ruta no es visible")
    void validarVisibleShouldNotQueryNodoWhenRutaIsNotVisible() {
        givenRutaIsNotVisible();

        assertThatThrownBy(this::validarVisible).isInstanceOf(ResourceNotFoundException.class);

        thenRepositoryWasNotQueried();
    }

    // --- arrange ---
    private void givenRepositoryHasNodos(UUID hitoId, List<Nodo> nodos) {
        when(nodoRepository.findVisiblesByRutaIdAndHitoId(RUTA_ID, hitoId)).thenReturn(nodos);
    }

    private void givenRutaIsNotVisible() {
        doThrow(new ResourceNotFoundException("ruta", "id", RUTA_ID)).when(rutaService).validarVisible(RUTA_ID);
    }

    private void givenHitoDoesNotBelongToRuta() {
        doThrow(new ResourceNotFoundException("hito", "id", HITO_ID))
                .when(hitoService).validarPertenencia(RUTA_ID, HITO_ID);
    }

    private void givenNodoIsVisible(boolean visible) {
        when(nodoRepository.existsVisibleByIdAndRutaId(NODO_ID, RUTA_ID)).thenReturn(visible);
    }

    // --- helpers ---
    private Nodo nodo() {
        return Nodo.builder().id(NODO_ID).rutaId(RUTA_ID).build();
    }

    // --- act ---
    private List<Nodo> listar(UUID hitoId) {
        return nodoService.listar(RUTA_ID, hitoId);
    }

    private void validarVisible() {
        nodoService.validarVisible(RUTA_ID, NODO_ID);
    }

    // --- assert ---
    private void thenResultIsAndHitoWasNotValidated(List<Nodo> result, List<Nodo> expected) {
        assertThat(result).isSameAs(expected);
        verify(rutaService).validarVisible(RUTA_ID);
        verifyNoInteractions(hitoService);
    }

    private void thenRutaAndHitoWereValidatedBeforeQuery() {
        InOrder order = inOrder(rutaService, hitoService, nodoRepository);
        order.verify(rutaService).validarVisible(RUTA_ID);
        order.verify(hitoService).validarPertenencia(RUTA_ID, HITO_ID);
        order.verify(nodoRepository).findVisiblesByRutaIdAndHitoId(RUTA_ID, HITO_ID);
    }

    private void thenNothingElseWasCalled() {
        verifyNoInteractions(nodoRepository, hitoService);
    }

    private void thenRepositoryWasNotQueried() {
        verifyNoInteractions(nodoRepository);
    }

    private void thenRutaWasValidated() {
        verify(rutaService).validarVisible(RUTA_ID);
    }
}
