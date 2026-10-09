package com.xpedia.backend.domain.service.microleccion;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.domain.repository.microleccion.MicroleccionRepository;
import com.xpedia.backend.domain.service.nodo.NodoService;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MicroleccionServiceTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();

    @Mock
    private NodoService nodoService;

    @Mock
    private MicroleccionRepository microleccionRepository;

    @InjectMocks
    private MicroleccionService microleccionService;

    @Test
    @DisplayName("Devuelve el material aprobado del repositorio")
    void listarShouldReturnRepositoryMaterialWhenNodoIsVisible() {
        List<Microleccion> material = givenRepositoryReturnsMaterial();

        List<Microleccion> result = listar();

        assertThat(result).isSameAs(material);
    }

    @Test
    @DisplayName("Valida la visibilidad del nodo antes de consultar el material")
    void listarShouldValidateVisibilityBeforeQueryingMaterial() {
        givenRepositoryReturnsMaterial();

        listar();

        thenNodoValidatedBeforeRepositoryQuery();
    }

    @Test
    @DisplayName("Devuelve lista vacía cuando el nodo visible no tiene material")
    void listarShouldReturnEmptyListWhenNodoHasNoMaterial() {
        givenRepositoryReturnsNoMaterial();

        List<Microleccion> result = listar();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("No consulta el material cuando el nodo no es visible")
    void listarShouldNotQueryMaterialWhenNodoIsNotVisible() {
        givenNodoIsNotVisible();

        assertThatThrownBy(this::listar).isInstanceOf(ResourceNotFoundException.class);

        thenRepositoryWasNotQueried();
    }

    // --- arrange ---
    private List<Microleccion> givenRepositoryReturnsMaterial() {
        List<Microleccion> material = List.of(Microleccion.builder().id(UUID.randomUUID()).build());
        when(microleccionRepository.findAprobadasByNodo(RUTA_ID, NODO_ID)).thenReturn(material);
        return material;
    }

    private void givenRepositoryReturnsNoMaterial() {
        when(microleccionRepository.findAprobadasByNodo(RUTA_ID, NODO_ID)).thenReturn(List.of());
    }

    private void givenNodoIsNotVisible() {
        doThrow(new ResourceNotFoundException("nodo", "id", NODO_ID))
                .when(nodoService).validarVisible(RUTA_ID, NODO_ID);
    }

    // --- act ---
    private List<Microleccion> listar() {
        return microleccionService.listar(RUTA_ID, NODO_ID);
    }

    // --- assert ---
    private void thenNodoValidatedBeforeRepositoryQuery() {
        InOrder order = inOrder(nodoService, microleccionRepository);
        order.verify(nodoService).validarVisible(RUTA_ID, NODO_ID);
        order.verify(microleccionRepository).findAprobadasByNodo(RUTA_ID, NODO_ID);
    }

    private void thenRepositoryWasNotQueried() {
        verifyNoInteractions(microleccionRepository);
    }
}
