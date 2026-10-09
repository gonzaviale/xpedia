package com.xpedia.backend.infrastructure.presentation.mapper.nodo;

import com.xpedia.backend.domain.dto.nodo.ListarNodosRequest;
import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.dto.nodo.NodoItem;
import com.xpedia.backend.domain.model.enums.TipoNodo;
import com.xpedia.backend.infrastructure.presentation.dto.nodo.NodoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NodoPresentationMapperTest {

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

    private NodoPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NodoPresentationMapper();
    }

    @Test
    @DisplayName("Construye el request con los ids de ruta e hito")
    void toRequestShouldBuildRequestWithRutaAndHitoIds() {
        ListarNodosRequest request = mapper.toRequest(RUTA_ID, HITO_ID);

        assertThat(request.rutaId()).isEqualTo(RUTA_ID);
        assertThat(request.hitoId()).isEqualTo(HITO_ID);
    }

    @Test
    @DisplayName("Construye el request con hito nulo cuando no se filtra por hito")
    void toRequestShouldBuildRequestWithNullHitoIdWhenNoFilter() {
        ListarNodosRequest request = mapper.toRequest(RUTA_ID, null);

        assertThat(request.hitoId()).isNull();
    }

    @Test
    @DisplayName("Mapea cada campo del item a la respuesta")
    void toResponseShouldMapEveryItemField() {
        ListarNodosResponse response = new ListarNodosResponse(List.of(nodoItem()));

        List<NodoResponse> result = mapper.toResponse(response);

        thenResponseHasEveryItemField(result);
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando no hay nodos")
    void toResponseShouldReturnEmptyListWhenThereAreNoNodos() {
        List<NodoResponse> result = mapper.toResponse(new ListarNodosResponse(List.of()));

        assertThat(result).isEmpty();
    }

    // --- helpers ---
    private NodoItem nodoItem() {
        return new NodoItem(
                NODO_ID,
                RUTA_ID,
                HITO_ID,
                RAMA_ID,
                HABILIDAD_ID,
                CODIGO,
                TITULO,
                RESUMEN,
                TipoNodo.OPCIONAL,
                NIVEL,
                MINUTOS_ESTIMADOS,
                PALABRAS_CLAVE,
                POSICION,
                List.of(PRERREQUISITO_ID));
    }

    // --- assert ---
    private void thenResponseHasEveryItemField(List<NodoResponse> result) {
        assertThat(result).hasSize(1);
        NodoResponse nodo = result.getFirst();
        assertThat(nodo.id()).isEqualTo(NODO_ID);
        assertThat(nodo.rutaId()).isEqualTo(RUTA_ID);
        assertThat(nodo.hitoId()).isEqualTo(HITO_ID);
        assertThat(nodo.ramaId()).isEqualTo(RAMA_ID);
        assertThat(nodo.habilidadId()).isEqualTo(HABILIDAD_ID);
        assertThat(nodo.codigo()).isEqualTo(CODIGO);
        assertThat(nodo.titulo()).isEqualTo(TITULO);
        assertThat(nodo.resumen()).isEqualTo(RESUMEN);
        assertThat(nodo.tipo()).isEqualTo(TipoNodo.OPCIONAL);
        assertThat(nodo.nivel()).isEqualTo(NIVEL);
        assertThat(nodo.minutosEstimados()).isEqualTo(MINUTOS_ESTIMADOS);
        assertThat(nodo.palabrasClave()).isEqualTo(PALABRAS_CLAVE);
        assertThat(nodo.posicion()).isEqualTo(POSICION);
        assertThat(nodo.prerrequisitoIds()).containsExactly(PRERREQUISITO_ID);
    }
}
