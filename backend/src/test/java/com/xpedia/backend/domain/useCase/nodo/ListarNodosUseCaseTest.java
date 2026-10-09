package com.xpedia.backend.domain.useCase.nodo;

import com.xpedia.backend.domain.dto.nodo.ListarNodosRequest;
import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.dto.nodo.NodoItem;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.nodo.ListarNodosMapper;
import com.xpedia.backend.domain.model.enums.TipoNodo;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.service.nodo.NodoService;
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
class ListarNodosUseCaseTest {

    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID HITO_ID = UUID.randomUUID();
    private static final UUID RAMA_ID = UUID.randomUUID();
    private static final UUID HABILIDAD_ID = UUID.randomUUID();
    private static final UUID PRERREQUISITO_ID = UUID.randomUUID();
    private static final String CODIGO = "A1";
    private static final String TITULO = "Escucha activa";
    private static final String RESUMEN = "Resumen del nodo";
    private static final Short NIVEL = 2;
    private static final Short MINUTOS_ESTIMADOS = 30;
    private static final Short POSICION = 3;
    private static final List<String> PALABRAS_CLAVE = List.of("comunicación", "cliente");

    @Mock
    private NodoService nodoService;

    private ListarNodosUseCase listarNodosUseCase;

    @BeforeEach
    void setUp() {
        listarNodosUseCase = new ListarNodosUseCase(nodoService, new ListarNodosMapper());
    }

    @Test
    @DisplayName("Lista los nodos de la ruta y el hito pedidos y los devuelve mapeados")
    void executeShouldReturnMappedNodosOfRutaAndHito() {
        givenServiceListsNodos(List.of(nodo()));

        ListarNodosResponse response = listar();

        thenResponseHasMappedNodo(response);
    }

    @Test
    @DisplayName("Delega la consulta al servicio con los ids de ruta e hito")
    void executeShouldDelegateToServiceWithRutaAndHitoIds() {
        givenServiceListsNodos(List.of());

        listar();

        thenServiceListedNodosOfRutaAndHito();
    }

    @Test
    @DisplayName("Propaga el error de dominio del servicio sin convertirlo")
    void executeShouldPropagateDomainErrorWhenServiceFails() {
        ResourceNotFoundException error = new ResourceNotFoundException("ruta", "id", RUTA_ID);
        givenServiceFails(error);

        assertThatThrownBy(this::listar).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceListsNodos(List<Nodo> nodos) {
        when(nodoService.listar(RUTA_ID, HITO_ID)).thenReturn(nodos);
    }

    private void givenServiceFails(ResourceNotFoundException error) {
        when(nodoService.listar(RUTA_ID, HITO_ID)).thenThrow(error);
    }

    // --- helpers ---
    private Nodo nodo() {
        return Nodo.builder()
                .id(NODO_ID)
                .rutaId(RUTA_ID)
                .hitoId(HITO_ID)
                .ramaId(RAMA_ID)
                .habilidadId(HABILIDAD_ID)
                .codigo(CODIGO)
                .titulo(TITULO)
                .resumen(RESUMEN)
                .tipo(TipoNodo.NUCLEO)
                .nivel(NIVEL)
                .minutosEstimados(MINUTOS_ESTIMADOS)
                .palabrasClave(PALABRAS_CLAVE)
                .posicion(POSICION)
                .prerrequisitoIds(List.of(PRERREQUISITO_ID))
                .build();
    }

    // --- act ---
    private ListarNodosResponse listar() {
        return listarNodosUseCase.execute(new ListarNodosRequest(RUTA_ID, HITO_ID));
    }

    // --- assert ---
    private void thenServiceListedNodosOfRutaAndHito() {
        verify(nodoService).listar(RUTA_ID, HITO_ID);
    }

    private void thenResponseHasMappedNodo(ListarNodosResponse response) {
        assertThat(response.content()).hasSize(1);
        NodoItem item = response.content().getFirst();
        assertThat(item.id()).isEqualTo(NODO_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.hitoId()).isEqualTo(HITO_ID);
        assertThat(item.ramaId()).isEqualTo(RAMA_ID);
        assertThat(item.habilidadId()).isEqualTo(HABILIDAD_ID);
        assertThat(item.codigo()).isEqualTo(CODIGO);
        assertThat(item.titulo()).isEqualTo(TITULO);
        assertThat(item.resumen()).isEqualTo(RESUMEN);
        assertThat(item.tipo()).isEqualTo(TipoNodo.NUCLEO);
        assertThat(item.nivel()).isEqualTo(NIVEL);
        assertThat(item.minutosEstimados()).isEqualTo(MINUTOS_ESTIMADOS);
        assertThat(item.palabrasClave()).isEqualTo(PALABRAS_CLAVE);
        assertThat(item.posicion()).isEqualTo(POSICION);
        assertThat(item.prerrequisitoIds()).containsExactly(PRERREQUISITO_ID);
    }
}
