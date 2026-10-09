package com.xpedia.backend.domain.service.reto;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.repository.reto.RetoRepository;
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
class RetoServiceTest {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID RETO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000001");

    @Mock
    private NodoService nodoService;

    @Mock
    private RetoRepository retoRepository;

    @InjectMocks
    private RetoService retoService;

    @Test
    @DisplayName("Valida el nodo visible antes de consultar los retos del nodo")
    void listarShouldValidateNodoBeforeQueryingRetos() {
        givenRepositoryListsRetos(List.of(reto()));

        listar();

        thenNodoValidatedBeforeListing();
    }

    @Test
    @DisplayName("Devuelve los retos aprobados del nodo")
    void listarShouldReturnRetosFromRepository() {
        Reto reto = reto();
        givenRepositoryListsRetos(List.of(reto));

        List<Reto> result = listar();

        assertThat(result).containsExactly(reto);
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando el nodo no tiene retos")
    void listarShouldReturnEmptyListWhenNodoHasNoRetos() {
        givenRepositoryListsRetos(List.of());

        List<Reto> result = listar();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("No consulta los retos cuando el nodo no es visible")
    void listarShouldNotQueryRetosWhenNodoIsNotVisible() {
        givenNodoIsNotVisible();

        assertThatThrownBy(this::listar).isInstanceOf(ResourceNotFoundException.class);

        thenRepositoryNotQueried();
    }

    @Test
    @DisplayName("Valida el nodo visible antes de consultar el reto")
    void obtenerShouldValidateNodoBeforeQueryingReto() {
        givenRepositoryFindsReto(Optional.of(reto()));

        obtener();

        thenNodoValidatedBeforeObtaining();
    }

    @Test
    @DisplayName("Devuelve el reto aprobado cuando existe en el nodo")
    void obtenerShouldReturnRetoFromRepository() {
        Reto reto = reto();
        givenRepositoryFindsReto(Optional.of(reto));

        Reto result = obtener();

        assertThat(result).isSameAs(reto);
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el reto no existe o está oculto")
    void obtenerShouldThrowResourceNotFoundWhenRetoIsAbsent() {
        givenRepositoryFindsReto(Optional.empty());

        assertThatThrownBy(this::obtener).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("No consulta el reto cuando el nodo no es visible")
    void obtenerShouldNotQueryRetoWhenNodoIsNotVisible() {
        givenNodoIsNotVisible();

        assertThatThrownBy(this::obtener).isInstanceOf(ResourceNotFoundException.class);

        thenRepositoryNotQueried();
    }

    // --- arrange ---
    private void givenRepositoryListsRetos(List<Reto> retos) {
        when(retoRepository.findAprobadosByNodo(RUTA_ID, NODO_ID)).thenReturn(retos);
    }

    private void givenRepositoryFindsReto(Optional<Reto> reto) {
        when(retoRepository.findAprobadoById(RUTA_ID, NODO_ID, RETO_ID)).thenReturn(reto);
    }

    private void givenNodoIsNotVisible() {
        doThrow(new ResourceNotFoundException("nodo", "id", NODO_ID))
                .when(nodoService).validarVisible(RUTA_ID, NODO_ID);
    }

    // --- helpers ---
    private Reto reto() {
        return Reto.builder()
                .id(RETO_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .titulo("Reto 01")
                .build();
    }

    // --- act ---
    private List<Reto> listar() {
        return retoService.listar(RUTA_ID, NODO_ID);
    }

    private Reto obtener() {
        return retoService.obtener(RUTA_ID, NODO_ID, RETO_ID);
    }

    // --- assert ---
    private void thenNodoValidatedBeforeListing() {
        InOrder order = inOrder(nodoService, retoRepository);
        order.verify(nodoService).validarVisible(RUTA_ID, NODO_ID);
        order.verify(retoRepository).findAprobadosByNodo(RUTA_ID, NODO_ID);
    }

    private void thenNodoValidatedBeforeObtaining() {
        InOrder order = inOrder(nodoService, retoRepository);
        order.verify(nodoService).validarVisible(RUTA_ID, NODO_ID);
        order.verify(retoRepository).findAprobadoById(RUTA_ID, NODO_ID, RETO_ID);
    }

    private void thenRepositoryNotQueried() {
        verifyNoInteractions(retoRepository);
    }
}
