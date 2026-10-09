package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.hito.HitoItem;
import com.xpedia.backend.domain.dto.hito.ListarHitosRequest;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.domain.dto.nodo.ListarNodosRequest;
import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.dto.nodo.NodoItem;
import com.xpedia.backend.domain.model.enums.TipoNodo;
import com.xpedia.backend.domain.useCase.hito.ListarHitosUseCase;
import com.xpedia.backend.domain.useCase.nodo.ListarNodosUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.hito.HitoPresentationMapper;
import com.xpedia.backend.infrastructure.presentation.mapper.nodo.NodoPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContenidoRutaControllerTest {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID RAMA_ID = UUID.fromString("00000000-0000-0000-0000-000000000601");
    private static final UUID HABILIDAD_ID = UUID.fromString("00000000-0000-0000-0000-000000000701");
    private static final UUID PRERREQUISITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000502");
    private static final String HITOS_URL = "/api/rutas/" + RUTA_ID + "/hitos";
    private static final String NODOS_URL = "/api/rutas/" + RUTA_ID + "/nodos";
    private static final Short POSICION = 1;
    private static final String TITULO_HITO = "Primer hito";
    private static final String OBJETIVO = "Aprender lo básico";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("8.5");
    private static final String EVIDENCIA_ESPERADA = "Un informe";
    private static final String CODIGO = "A1";
    private static final String TITULO_NODO = "Escucha activa";
    private static final String RESUMEN = "Resumen del nodo";
    private static final Short NIVEL = 2;
    private static final Short MINUTOS_ESTIMADOS = 30;

    private MockMvc mockMvc;
    private ListarHitosUseCase listarHitosUseCase;
    private ListarNodosUseCase listarNodosUseCase;

    @BeforeEach
    void setUp() {
        listarHitosUseCase = mock(ListarHitosUseCase.class);
        listarNodosUseCase = mock(ListarNodosUseCase.class);
        ContenidoRutaController controller = new ContenidoRutaController(
                listarHitosUseCase,
                listarNodosUseCase,
                new HitoPresentationMapper(),
                new NodoPresentationMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET hitos devuelve 200 con los hitos de la ruta")
    void listarHitosShouldReturn200WithHitosOfRuta() throws Exception {
        givenUseCaseListsHitos(new ListarHitosResponse(List.of(hitoItem())));

        ResultActions result = performListarHitos();

        thenOkWithHito(result);
    }

    @Test
    @DisplayName("GET hitos pasa el id de la ruta al caso de uso")
    void listarHitosShouldPassRutaIdToUseCase() throws Exception {
        givenUseCaseListsHitos(new ListarHitosResponse(List.of()));

        performListarHitos();

        thenHitosUseCaseReceivedRutaId();
    }

    @Test
    @DisplayName("GET hitos devuelve 200 con un arreglo vacío cuando la ruta no tiene hitos")
    void listarHitosShouldReturn200WithEmptyArrayWhenRutaHasNoHitos() throws Exception {
        givenUseCaseListsHitos(new ListarHitosResponse(List.of()));

        ResultActions result = performListarHitos();

        thenOkWithEmptyArray(result);
    }

    @Test
    @DisplayName("GET nodos devuelve 200 con los nodos de la ruta")
    void listarNodosShouldReturn200WithNodosOfRuta() throws Exception {
        givenUseCaseListsNodos(new ListarNodosResponse(List.of(nodoItem())));

        ResultActions result = performListarNodos(NODOS_URL);

        thenOkWithNodo(result);
    }

    @Test
    @DisplayName("GET nodos pasa el id de la ruta y el hito filtrado al caso de uso")
    void listarNodosShouldPassRutaIdAndHitoIdToUseCase() throws Exception {
        givenUseCaseListsNodos(new ListarNodosResponse(List.of()));

        performListarNodos(NODOS_URL + "?hitoId=" + HITO_ID);

        thenNodosUseCaseReceived(RUTA_ID, HITO_ID);
    }

    @Test
    @DisplayName("GET nodos pasa hito nulo al caso de uso cuando no se filtra por hito")
    void listarNodosShouldPassNullHitoIdWhenNoFilter() throws Exception {
        givenUseCaseListsNodos(new ListarNodosResponse(List.of()));

        performListarNodos(NODOS_URL);

        thenNodosUseCaseReceived(RUTA_ID, null);
    }

    @Test
    @DisplayName("GET nodos devuelve 200 con un arreglo vacío cuando la ruta no tiene nodos")
    void listarNodosShouldReturn200WithEmptyArrayWhenRutaHasNoNodos() throws Exception {
        givenUseCaseListsNodos(new ListarNodosResponse(List.of()));

        ResultActions result = performListarNodos(NODOS_URL);

        thenOkWithEmptyArray(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/rutas/id-invalido/hitos",
            "/api/rutas/id-invalido/nodos",
            "/api/rutas/00000000-0000-0000-0000-000000000201/nodos?hitoId=invalido"
    })
    @DisplayName("Devuelve 400 y no invoca los casos de uso cuando un UUID es inválido")
    void endpointsShouldReturn400WithoutInvokingUseCasesWhenUuidIsInvalid(String path) throws Exception {
        ResultActions result = performGet(path);

        thenBadRequestWithoutUseCases(result);
    }

    // --- arrange ---
    private void givenUseCaseListsHitos(ListarHitosResponse response) {
        when(listarHitosUseCase.execute(any(ListarHitosRequest.class))).thenReturn(response);
    }

    private void givenUseCaseListsNodos(ListarNodosResponse response) {
        when(listarNodosUseCase.execute(any(ListarNodosRequest.class))).thenReturn(response);
    }

    // --- helpers ---
    private HitoItem hitoItem() {
        return new HitoItem(
                HITO_ID,
                RUTA_ID,
                POSICION,
                TITULO_HITO,
                OBJETIVO,
                HORAS_ESTIMADAS,
                false,
                EVIDENCIA_ESPERADA);
    }

    private NodoItem nodoItem() {
        return new NodoItem(
                NODO_ID,
                RUTA_ID,
                HITO_ID,
                RAMA_ID,
                HABILIDAD_ID,
                CODIGO,
                TITULO_NODO,
                RESUMEN,
                TipoNodo.NUCLEO,
                NIVEL,
                MINUTOS_ESTIMADOS,
                List.of("comunicación"),
                POSICION,
                List.of(PRERREQUISITO_ID));
    }

    // --- act ---
    private ResultActions performListarHitos() throws Exception {
        return performGet(HITOS_URL);
    }

    private ResultActions performListarNodos(String url) throws Exception {
        return performGet(url);
    }

    private ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    // --- assert ---
    private void thenOkWithHito(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(HITO_ID.toString()))
                .andExpect(jsonPath("$[0].rutaId").value(RUTA_ID.toString()))
                .andExpect(jsonPath("$[0].posicion").value(POSICION.intValue()))
                .andExpect(jsonPath("$[0].titulo").value(TITULO_HITO))
                .andExpect(jsonPath("$[0].objetivo").value(OBJETIVO))
                .andExpect(jsonPath("$[0].horasEstimadas").value(HORAS_ESTIMADAS.doubleValue()))
                .andExpect(jsonPath("$[0].esFinal").value(false))
                .andExpect(jsonPath("$[0].evidenciaEsperada").value(EVIDENCIA_ESPERADA));
    }

    private void thenOkWithNodo(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(NODO_ID.toString()))
                .andExpect(jsonPath("$[0].rutaId").value(RUTA_ID.toString()))
                .andExpect(jsonPath("$[0].hitoId").value(HITO_ID.toString()))
                .andExpect(jsonPath("$[0].ramaId").value(RAMA_ID.toString()))
                .andExpect(jsonPath("$[0].habilidadId").value(HABILIDAD_ID.toString()))
                .andExpect(jsonPath("$[0].codigo").value(CODIGO))
                .andExpect(jsonPath("$[0].titulo").value(TITULO_NODO))
                .andExpect(jsonPath("$[0].resumen").value(RESUMEN))
                .andExpect(jsonPath("$[0].tipo").value("NUCLEO"))
                .andExpect(jsonPath("$[0].nivel").value(NIVEL.intValue()))
                .andExpect(jsonPath("$[0].minutosEstimados").value(MINUTOS_ESTIMADOS.intValue()))
                .andExpect(jsonPath("$[0].palabrasClave[0]").value("comunicación"))
                .andExpect(jsonPath("$[0].posicion").value(POSICION.intValue()))
                .andExpect(jsonPath("$[0].prerrequisitoIds[0]").value(PRERREQUISITO_ID.toString()));
    }

    private void thenOkWithEmptyArray(ResultActions result) throws Exception {
        result.andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    private void thenHitosUseCaseReceivedRutaId() {
        ArgumentCaptor<ListarHitosRequest> captor = ArgumentCaptor.forClass(ListarHitosRequest.class);
        verify(listarHitosUseCase).execute(captor.capture());
        assertThat(captor.getValue().rutaId()).isEqualTo(RUTA_ID);
    }

    private void thenNodosUseCaseReceived(UUID rutaId, UUID hitoId) {
        ArgumentCaptor<ListarNodosRequest> captor = ArgumentCaptor.forClass(ListarNodosRequest.class);
        verify(listarNodosUseCase).execute(captor.capture());
        assertThat(captor.getValue().rutaId()).isEqualTo(rutaId);
        assertThat(captor.getValue().hitoId()).isEqualTo(hitoId);
    }

    private void thenBadRequestWithoutUseCases(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(listarHitosUseCase, listarNodosUseCase);
    }
}
