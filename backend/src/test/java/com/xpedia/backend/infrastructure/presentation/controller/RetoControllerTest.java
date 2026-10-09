package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.reto.CriterioRubricaItem;
import com.xpedia.backend.domain.dto.reto.ListarRetosRequest;
import com.xpedia.backend.domain.dto.reto.ListarRetosResponse;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoRequest;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoResponse;
import com.xpedia.backend.domain.dto.reto.RetoItem;
import com.xpedia.backend.domain.dto.reto.RubricaItem;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.useCase.reto.ListarRetosUseCase;
import com.xpedia.backend.domain.useCase.reto.ObtenerRetoUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.reto.RetoPresentationMapper;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
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

class RetoControllerTest {

    private static final String RUTA_ID_TEXT = "00000000-0000-0000-0000-000000000201";
    private static final String NODO_ID_TEXT = "00000000-0000-0000-0000-000000000501";
    private static final String RETO_ID_TEXT = "d3000000-0000-4000-8000-000000000001";
    private static final UUID RUTA_ID = UUID.fromString(RUTA_ID_TEXT);
    private static final UUID NODO_ID = UUID.fromString(NODO_ID_TEXT);
    private static final UUID RETO_ID = UUID.fromString(RETO_ID_TEXT);
    private static final UUID HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-08T10:00:00-03:00");
    private static final String BASE_PATH = "/api/rutas/" + RUTA_ID_TEXT + "/nodos/" + NODO_ID_TEXT + "/retos";
    private static final String RETO_PATH = BASE_PATH + "/" + RETO_ID_TEXT;
    private static final String RUTA_INVALIDA_PATH = "/api/rutas/texto/nodos/" + NODO_ID_TEXT + "/retos";
    private static final String NODO_INVALIDO_PATH = "/api/rutas/" + RUTA_ID_TEXT + "/nodos/texto/retos";
    private static final String RETO_INVALIDO_PATH = BASE_PATH + "/texto";

    private MockMvc mockMvc;
    private ListarRetosUseCase listarRetosUseCase;
    private ObtenerRetoUseCase obtenerRetoUseCase;

    @BeforeEach
    void setUp() {
        listarRetosUseCase = mock(ListarRetosUseCase.class);
        obtenerRetoUseCase = mock(ObtenerRetoUseCase.class);
        RetoController retoController = new RetoController(
                listarRetosUseCase, obtenerRetoUseCase, new RetoPresentationMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(retoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Devuelve 200 con los retos del nodo y su rúbrica")
    void listarShouldReturn200WithRetosAndRubrica() throws Exception {
        givenNodoHasRetos();

        ResultActions result = performListar();

        thenOkWithRetoList(result);
    }

    @Test
    @DisplayName("Devuelve 200 con un array vacío cuando el nodo no tiene retos")
    void listarShouldReturn200WithEmptyArrayWhenNodoHasNoRetos() throws Exception {
        givenNodoHasNoRetos();

        ResultActions result = performListar();

        thenOkWithEmptyArray(result);
    }

    @Test
    @DisplayName("Pasa la ruta y el nodo del path al caso de uso de listar")
    void listarShouldPassPathIdsToUseCase() throws Exception {
        givenNodoHasNoRetos();

        performListar();

        thenListarUseCaseReceivedPathIds();
    }

    @Test
    @DisplayName("Devuelve 404 al listar cuando el nodo no es visible")
    void listarShouldReturn404WhenNodoIsNotVisible() throws Exception {
        givenListarFailsWithNotFound();

        ResultActions result = performListar();

        thenNotFound(result);
    }

    @Test
    @DisplayName("Devuelve 200 con la consigna y la rúbrica del reto")
    void obtenerShouldReturn200WithRetoAndRubrica() throws Exception {
        givenRetoExists();

        ResultActions result = performObtener();

        thenOkWithRetoDetail(result);
    }

    @Test
    @DisplayName("Pasa la ruta, el nodo y el reto del path al caso de uso de obtener")
    void obtenerShouldPassPathIdsToUseCase() throws Exception {
        givenRetoExists();

        performObtener();

        thenObtenerUseCaseReceivedPathIds();
    }

    @Test
    @DisplayName("Devuelve 404 cuando el reto está oculto o no existe")
    void obtenerShouldReturn404WhenRetoIsHidden() throws Exception {
        givenObtenerFailsWithNotFound();

        ResultActions result = performObtener();

        thenNotFound(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {RUTA_INVALIDA_PATH, NODO_INVALIDO_PATH, RETO_INVALIDO_PATH})
    @DisplayName("Devuelve 400 sin invocar casos de uso cuando algún id del path no es UUID")
    void getShouldReturn400WhenPathIdIsNotUuid(String path) throws Exception {
        ResultActions result = performGet(path);

        thenBadRequestWithoutUseCases(result);
    }

    // --- arrange ---
    private void givenNodoHasRetos() {
        when(listarRetosUseCase.execute(any(ListarRetosRequest.class)))
                .thenReturn(new ListarRetosResponse(List.of(retoItem())));
    }

    private void givenNodoHasNoRetos() {
        when(listarRetosUseCase.execute(any(ListarRetosRequest.class)))
                .thenReturn(new ListarRetosResponse(List.of()));
    }

    private void givenListarFailsWithNotFound() {
        when(listarRetosUseCase.execute(any(ListarRetosRequest.class)))
                .thenThrow(new ResourceNotFoundException("nodo", "id", NODO_ID));
    }

    private void givenRetoExists() {
        when(obtenerRetoUseCase.execute(any(ObtenerRetoRequest.class))).thenReturn(obtenerResponse());
    }

    private void givenObtenerFailsWithNotFound() {
        when(obtenerRetoUseCase.execute(any(ObtenerRetoRequest.class)))
                .thenThrow(new ResourceNotFoundException("reto", "id", RETO_ID));
    }

    // --- helpers ---
    private RetoItem retoItem() {
        return new RetoItem(RETO_ID, RUTA_ID, NODO_ID, HITO_ID, TipoReto.ENSAYO, "Reto 01", (short) 2,
                Map.of("consigna", "Responder"), "HUMANO", REVISADO_EN, rubricaItem());
    }

    private ObtenerRetoResponse obtenerResponse() {
        return new ObtenerRetoResponse(RETO_ID, RUTA_ID, NODO_ID, HITO_ID, TipoReto.ENSAYO, "Reto 01", (short) 2,
                Map.of("consigna", "Responder"), "HUMANO", REVISADO_EN, rubricaItem());
    }

    private RubricaItem rubricaItem() {
        CriterioRubricaItem criterio = new CriterioRubricaItem(
                CRITERIO_ID, (short) 1, "Política", null, (short) 3, new BigDecimal("0.75"), true);
        return new RubricaItem(
                RUBRICA_ID, "Escritura", null, new BigDecimal("1.00"), new BigDecimal("2.25"), List.of(criterio));
    }

    // --- act ---
    private ResultActions performListar() throws Exception {
        return performGet(BASE_PATH);
    }

    private ResultActions performObtener() throws Exception {
        return performGet(RETO_PATH);
    }

    private ResultActions performGet(String path) throws Exception {
        return mockMvc.perform(get(path));
    }

    // --- assert ---
    private void thenOkWithRetoList(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(RETO_ID.toString()))
                .andExpect(jsonPath("$[0].tipo").value("ENSAYO"))
                .andExpect(jsonPath("$[0].titulo").value("Reto 01"))
                .andExpect(jsonPath("$[0].contenido.consigna").value("Responder"))
                .andExpect(jsonPath("$[0].rubrica.id").value(RUBRICA_ID.toString()))
                .andExpect(jsonPath("$[0].rubrica.puntajeMaximo").value(2.25));
    }

    private void thenOkWithEmptyArray(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    private void thenOkWithRetoDetail(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RETO_ID.toString()))
                .andExpect(jsonPath("$.tipo").value("ENSAYO"))
                .andExpect(jsonPath("$.rubrica.puntajeAprobacion").value(1.00))
                .andExpect(jsonPath("$.rubrica.puntajeMaximo").value(2.25))
                .andExpect(jsonPath("$.rubrica.criterios[0].nombre").value("Política"))
                .andExpect(jsonPath("$.rubrica.criterios[0].eliminatorio").value(true));
    }

    private void thenListarUseCaseReceivedPathIds() {
        ArgumentCaptor<ListarRetosRequest> captor = ArgumentCaptor.forClass(ListarRetosRequest.class);
        verify(listarRetosUseCase).execute(captor.capture());
        assertThat(captor.getValue().rutaId()).isEqualTo(RUTA_ID);
        assertThat(captor.getValue().nodoId()).isEqualTo(NODO_ID);
    }

    private void thenObtenerUseCaseReceivedPathIds() {
        ArgumentCaptor<ObtenerRetoRequest> captor = ArgumentCaptor.forClass(ObtenerRetoRequest.class);
        verify(obtenerRetoUseCase).execute(captor.capture());
        assertThat(captor.getValue().rutaId()).isEqualTo(RUTA_ID);
        assertThat(captor.getValue().nodoId()).isEqualTo(NODO_ID);
        assertThat(captor.getValue().retoId()).isEqualTo(RETO_ID);
    }

    private void thenNotFound(ResultActions result) throws Exception {
        result.andExpect(status().isNotFound());
    }

    private void thenBadRequestWithoutUseCases(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest());
        verifyNoInteractions(listarRetosUseCase, obtenerRetoUseCase);
    }
}
