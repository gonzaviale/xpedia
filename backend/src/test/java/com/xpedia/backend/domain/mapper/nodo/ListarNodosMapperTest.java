package com.xpedia.backend.domain.mapper.nodo;

import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.dto.nodo.NodoItem;
import com.xpedia.backend.domain.model.enums.TipoNodo;
import com.xpedia.backend.domain.model.nodo.Nodo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListarNodosMapperTest {

    private static final UUID PRIMER_ID = UUID.randomUUID();
    private static final UUID SEGUNDO_ID = UUID.randomUUID();
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

    private ListarNodosMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ListarNodosMapper();
    }

    @Test
    @DisplayName("Mapea cada campo del nodo al item")
    void toResponseShouldMapEveryNodoField() {
        List<Nodo> nodos = List.of(nodoCompleto());

        ListarNodosResponse response = mapear(nodos);

        thenItemHasEveryNodoField(response.content().getFirst());
    }

    @Test
    @DisplayName("Conserva el orden de los nodos")
    void toResponseShouldKeepNodosOrder() {
        List<Nodo> nodos = List.of(nodoConId(PRIMER_ID), nodoConId(SEGUNDO_ID));

        ListarNodosResponse response = mapear(nodos);

        thenItemIdsAre(response, PRIMER_ID, SEGUNDO_ID);
    }

    @Test
    @DisplayName("Conserva los nulos de un nodo sin datos")
    void toResponseShouldKeepNullsWhenNodoHasNoData() {
        List<Nodo> nodos = List.of(new Nodo());

        ListarNodosResponse response = mapear(nodos);

        thenItemHasOnlyNulls(response.content().getFirst());
    }

    @Test
    @DisplayName("Devuelve contenido vacío cuando no hay nodos")
    void toResponseShouldReturnEmptyContentWhenThereAreNoNodos() {
        ListarNodosResponse response = mapear(List.of());

        assertThat(response.content()).isEmpty();
    }

    // --- helpers ---
    private Nodo nodoCompleto() {
        return Nodo.builder()
                .id(PRIMER_ID)
                .rutaId(RUTA_ID)
                .hitoId(HITO_ID)
                .ramaId(RAMA_ID)
                .habilidadId(HABILIDAD_ID)
                .codigo(CODIGO)
                .titulo(TITULO)
                .resumen(RESUMEN)
                .tipo(TipoNodo.TEMA)
                .nivel(NIVEL)
                .minutosEstimados(MINUTOS_ESTIMADOS)
                .palabrasClave(PALABRAS_CLAVE)
                .posicion(POSICION)
                .prerrequisitoIds(List.of(PRERREQUISITO_ID))
                .build();
    }

    private Nodo nodoConId(UUID id) {
        return Nodo.builder().id(id).build();
    }

    // --- act ---
    private ListarNodosResponse mapear(List<Nodo> nodos) {
        return mapper.toResponse(nodos);
    }

    // --- assert ---
    private void thenItemHasEveryNodoField(NodoItem item) {
        assertThat(item.id()).isEqualTo(PRIMER_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.hitoId()).isEqualTo(HITO_ID);
        assertThat(item.ramaId()).isEqualTo(RAMA_ID);
        assertThat(item.habilidadId()).isEqualTo(HABILIDAD_ID);
        assertThat(item.codigo()).isEqualTo(CODIGO);
        assertThat(item.titulo()).isEqualTo(TITULO);
        assertThat(item.resumen()).isEqualTo(RESUMEN);
        assertThat(item.tipo()).isEqualTo(TipoNodo.TEMA);
        assertThat(item.nivel()).isEqualTo(NIVEL);
        assertThat(item.minutosEstimados()).isEqualTo(MINUTOS_ESTIMADOS);
        assertThat(item.palabrasClave()).isEqualTo(PALABRAS_CLAVE);
        assertThat(item.posicion()).isEqualTo(POSICION);
        assertThat(item.prerrequisitoIds()).containsExactly(PRERREQUISITO_ID);
    }

    private void thenItemIdsAre(ListarNodosResponse response, UUID first, UUID second) {
        assertThat(response.content()).extracting(NodoItem::id).containsExactly(first, second);
    }

    private void thenItemHasOnlyNulls(NodoItem item) {
        assertThat(item.id()).isNull();
        assertThat(item.rutaId()).isNull();
        assertThat(item.hitoId()).isNull();
        assertThat(item.ramaId()).isNull();
        assertThat(item.habilidadId()).isNull();
        assertThat(item.codigo()).isNull();
        assertThat(item.titulo()).isNull();
        assertThat(item.resumen()).isNull();
        assertThat(item.tipo()).isNull();
        assertThat(item.nivel()).isNull();
        assertThat(item.minutosEstimados()).isNull();
        assertThat(item.palabrasClave()).isNull();
        assertThat(item.posicion()).isNull();
        assertThat(item.prerrequisitoIds()).isNull();
    }
}
