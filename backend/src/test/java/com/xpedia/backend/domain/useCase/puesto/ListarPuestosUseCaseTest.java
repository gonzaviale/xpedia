package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ListarPuestosRequest;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosResponse;
import com.xpedia.backend.domain.mapper.puesto.ListarPuestosMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarPuestosUseCaseTest {

    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final int PAGE = 1;
    private static final int SIZE = 5;
    private static final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, Sort.by("nombre"));

    @Mock
    private PuestoService puestoService;

    private ListarPuestosUseCase listarPuestosUseCase;

    @BeforeEach
    void setUp() {
        listarPuestosUseCase = new ListarPuestosUseCase(puestoService, new ListarPuestosMapper());
    }

    @Test
    @DisplayName("Lista con la organización y la página ordenada por nombre")
    void executeShouldListWithOrganizacionAndPageSortedByNombre() {
        givenServiceReturnsEmptyPage();

        listar();

        thenServiceListedWithOrganizacionAndPageable();
    }

    @Test
    @DisplayName("Devuelve la página vacía con su paginación")
    void executeShouldReturnEmptyPage() {
        givenServiceReturnsEmptyPage();

        ListarPuestosResponse response = listar();

        thenResponseIsEmptyPage(response);
    }

    @Test
    @DisplayName("Propaga el error del servicio")
    void executeShouldPropagateErrorFromService() {
        IllegalStateException error = givenServiceThrows();

        assertThatThrownBy(this::listar).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceReturnsEmptyPage() {
        when(puestoService.listar(ORGANIZACION_ID, PAGEABLE)).thenReturn(Page.<Puesto>empty(PAGEABLE));
    }

    private IllegalStateException givenServiceThrows() {
        IllegalStateException error = new IllegalStateException("Fallo de consulta");
        when(puestoService.listar(ORGANIZACION_ID, PAGEABLE)).thenThrow(error);
        return error;
    }

    // --- act ---
    private ListarPuestosResponse listar() {
        return listarPuestosUseCase.execute(new ListarPuestosRequest(ORGANIZACION_ID, PAGE, SIZE));
    }

    // --- assert ---
    private void thenServiceListedWithOrganizacionAndPageable() {
        verify(puestoService).listar(ORGANIZACION_ID, PAGEABLE);
    }

    private void thenResponseIsEmptyPage(ListarPuestosResponse response) {
        assertThat(response.content()).isEmpty();
        assertThat(response.pageNumber()).isEqualTo(PAGE);
        assertThat(response.pageSize()).isEqualTo(SIZE);
        assertThat(response.totalElements()).isZero();
    }
}
