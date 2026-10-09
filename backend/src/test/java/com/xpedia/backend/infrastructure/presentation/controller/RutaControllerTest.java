package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaRequest;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaResponse;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.enums.EstadoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;
import com.xpedia.backend.domain.useCase.ruta.ListarRutasUseCase;
import com.xpedia.backend.domain.useCase.ruta.ObtenerRutaUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.ruta.RutaPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RutaControllerTest {

    private static final String BASE_PATH = "/api/rutas";
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final String SLUG = "atencion-al-cliente";
    private static final String TITULO = "Atención al cliente";
    private static final int DEFAULT_SIZE = 20;

    private MockMvc mockMvc;
    private ListarRutasUseCase listarRutasUseCase;
    private ObtenerRutaUseCase obtenerRutaUseCase;

    @BeforeEach
    void setUp() {
        listarRutasUseCase = mock(ListarRutasUseCase.class);
        obtenerRutaUseCase = mock(ObtenerRutaUseCase.class);
        RutaController controller = new RutaController(
                listarRutasUseCase, obtenerRutaUseCase, new RutaPresentationMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Aplica la paginación por defecto y devuelve 200 con la página vacía")
    void listarShouldReturn200WithEmptyPageWhenNoQueryParams() throws Exception {
        givenUseCaseListsEmptyPage(new ListarRutasRequest(null, null, 0, DEFAULT_SIZE));

        ResultActions result = performListar("");

        thenOkWithEmptyPage(result);
    }

    @Test
    @DisplayName("Pasa al caso de uso los filtros y la paginación explícitos")
    void listarShouldPassExplicitFiltersAndPagingToUseCase() throws Exception {
        ListarRutasRequest request = new ListarRutasRequest(TipoRuta.TECNICA, ObjetivoRuta.ARRANCAR, 2, 5);
        givenUseCaseListsEmptyPage(request);

        performListar("?tipo=TECNICA&objetivo=ARRANCAR&page=2&size=5");

        thenUseCaseListed(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "page=-1", "size=0", "size=-1", "size=101", "page=texto", "size=texto",
            "page=2147483648", "tipo=INVALIDA", "objetivo=INVALIDO", "tipo=tecnica"
    })
    @DisplayName("Devuelve 400 y no invoca casos de uso cuando los filtros o la paginación son inválidos")
    void listarShouldReturn400WhenQueryIsInvalid(String query) throws Exception {
        ResultActions result = performListar("?" + query);

        thenBadRequestWithErrors(result);
    }

    @Test
    @DisplayName("Devuelve 200 con el detalle de la ruta")
    void obtenerShouldReturn200WithRuta() throws Exception {
        givenUseCaseGetsRuta();

        ResultActions result = performObtener(RUTA_ID.toString());

        thenOkWithRuta(result);
    }

    @Test
    @DisplayName("Devuelve 400 cuando el id no es un UUID")
    void obtenerShouldReturn400WhenIdIsNotUuid() throws Exception {
        ResultActions result = performObtener("id-invalido");

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.id").exists());
        verifyNoInteractions(listarRutasUseCase, obtenerRutaUseCase);
    }

    @Test
    @DisplayName("Devuelve 404 cuando la ruta no existe")
    void obtenerShouldReturn404WhenRutaNotFound() throws Exception {
        givenUseCaseThrowsNotFound();

        ResultActions result = performObtener(RUTA_ID.toString());

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // --- arrange ---
    private void givenUseCaseListsEmptyPage(ListarRutasRequest request) {
        when(listarRutasUseCase.execute(request))
                .thenReturn(new ListarRutasResponse(List.of(), request.page(), request.size(), 0, 0, true, true));
    }

    private void givenUseCaseGetsRuta() {
        when(obtenerRutaUseCase.execute(new ObtenerRutaRequest(RUTA_ID))).thenReturn(rutaResponse());
    }

    private void givenUseCaseThrowsNotFound() {
        when(obtenerRutaUseCase.execute(any(ObtenerRutaRequest.class)))
                .thenThrow(new ResourceNotFoundException("ruta", "id", RUTA_ID));
    }

    // --- act ---
    private ResultActions performListar(String query) throws Exception {
        return mockMvc.perform(get(BASE_PATH + query));
    }

    private ResultActions performObtener(String id) throws Exception {
        return mockMvc.perform(get(BASE_PATH + "/" + id));
    }

    // --- assert ---
    private void thenOkWithEmptyPage(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.pageSize").value(DEFAULT_SIZE));
        verify(listarRutasUseCase).execute(new ListarRutasRequest(null, null, 0, DEFAULT_SIZE));
    }

    private void thenUseCaseListed(ListarRutasRequest request) {
        verify(listarRutasUseCase).execute(request);
    }

    private void thenBadRequestWithErrors(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isNotEmpty());
        verifyNoInteractions(listarRutasUseCase, obtenerRutaUseCase);
    }

    private void thenOkWithRuta(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(RUTA_ID.toString()))
                .andExpect(jsonPath("$.slug").value(SLUG))
                .andExpect(jsonPath("$.titulo").value(TITULO))
                .andExpect(jsonPath("$.tipo").value("TECNICA"))
                .andExpect(jsonPath("$.estado").value("PUBLICADA"));
    }

    // --- helpers ---
    private ObtenerRutaResponse rutaResponse() {
        return new ObtenerRutaResponse(
                RUTA_ID,
                SLUG,
                1,
                TITULO,
                TipoRuta.TECNICA,
                ObjetivoRuta.ARRANCAR,
                "Atender consultas por chat",
                null,
                "AR",
                new BigDecimal("10.5"),
                (short) 30,
                EstadoRuta.PUBLICADA,
                ValidacionRuta.BORRADOR_IA,
                null,
                OffsetDateTime.parse("2026-02-01T08:00:00Z"),
                OffsetDateTime.parse("2026-03-02T09:30:00Z"));
    }
}
