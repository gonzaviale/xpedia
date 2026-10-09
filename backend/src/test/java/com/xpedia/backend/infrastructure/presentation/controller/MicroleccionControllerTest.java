package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.microleccion.FuenteMicroleccionItem;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesRequest;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesResponse;
import com.xpedia.backend.domain.dto.microleccion.MicroleccionItem;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.useCase.microleccion.ListarMicroleccionesUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.microleccion.MicroleccionPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MicroleccionControllerTest {

    private static final String RUTA_ID_TEXT = "00000000-0000-0000-0000-000000000201";
    private static final String NODO_ID_TEXT = "00000000-0000-0000-0000-000000000501";
    private static final UUID RUTA_ID = UUID.fromString(RUTA_ID_TEXT);
    private static final UUID NODO_ID = UUID.fromString(NODO_ID_TEXT);
    private static final UUID MICROLECCION_ID = UUID.fromString("00000000-0000-0000-0000-000000000911");
    private static final UUID FUENTE_ID = UUID.fromString("00000000-0000-0000-0000-000000000901");
    private static final String PATH = "/api/rutas/" + RUTA_ID_TEXT + "/nodos/" + NODO_ID_TEXT + "/microlecciones";
    private static final String PATH_CON_RUTA_INVALIDA = "/api/rutas/texto/nodos/" + NODO_ID_TEXT + "/microlecciones";
    private static final String PATH_CON_NODO_INVALIDO = "/api/rutas/" + RUTA_ID_TEXT + "/nodos/texto/microlecciones";
    private static final String TITULO = "Lección inicial";
    private static final String FUENTE_TITULO = "Fuente global";
    private static final String FUENTE_URL = "https://example.org/material";
    private static final String ERROR_MESSAGE = "Nodo ausente";

    private MockMvc mockMvc;
    private ListarMicroleccionesUseCase listarMicroleccionesUseCase;

    @BeforeEach
    void setUp() {
        listarMicroleccionesUseCase = mock(ListarMicroleccionesUseCase.class);
        MicroleccionController controller = new MicroleccionController(
                listarMicroleccionesUseCase, new MicroleccionPresentationMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Devuelve 200 con un array vacío cuando el nodo no tiene material")
    void listarShouldReturn200WithEmptyArrayWhenNodoHasNoMaterial() throws Exception {
        givenUseCaseReturnsNoMaterial();

        ResultActions result = performListar(PATH);

        thenOkWithBody(result, "[]");
    }

    @Test
    @DisplayName("Invoca el caso de uso con los ids de la ruta y del nodo")
    void listarShouldInvokeUseCaseWithRutaAndNodoIds() throws Exception {
        givenUseCaseReturnsNoMaterial();

        performListar(PATH);

        thenUseCaseReceivedRutaAndNodoIds();
    }

    @Test
    @DisplayName("Devuelve la microlección con sus fuentes en el cuerpo")
    void listarShouldReturnMicroleccionWithFuentes() throws Exception {
        givenUseCaseReturnsMicroleccion();

        ResultActions result = performListar(PATH);

        thenBodyHasMicroleccionWithFuente(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {PATH_CON_RUTA_INVALIDA, PATH_CON_NODO_INVALIDO})
    @DisplayName("Devuelve 400 sin invocar el caso de uso cuando algún UUID es inválido")
    void listarShouldReturn400WithoutInvokingUseCaseWhenUuidIsInvalid(String path) throws Exception {
        ResultActions result = performListar(path);

        thenBadRequestWithoutUseCase(result);
    }

    @Test
    @DisplayName("Devuelve 404 con el mensaje cuando el caso de uso no encuentra el nodo")
    void listarShouldReturn404WhenUseCaseThrowsNotFound() throws Exception {
        givenUseCaseThrowsNotFound();

        ResultActions result = performListar(PATH);

        thenNotFoundWithMessage(result);
    }

    // --- arrange ---
    private void givenUseCaseReturnsNoMaterial() {
        when(listarMicroleccionesUseCase.execute(any(ListarMicroleccionesRequest.class)))
                .thenReturn(new ListarMicroleccionesResponse(List.of()));
    }

    private void givenUseCaseReturnsMicroleccion() {
        when(listarMicroleccionesUseCase.execute(any(ListarMicroleccionesRequest.class)))
                .thenReturn(new ListarMicroleccionesResponse(List.of(microleccionItem())));
    }

    private void givenUseCaseThrowsNotFound() {
        when(listarMicroleccionesUseCase.execute(any(ListarMicroleccionesRequest.class)))
                .thenThrow(new ResourceNotFoundException(ERROR_MESSAGE));
    }

    // --- act ---
    private ResultActions performListar(String path) throws Exception {
        return mockMvc.perform(get(path));
    }

    // --- assert ---
    private void thenOkWithBody(ResultActions result, String json) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    private void thenUseCaseReceivedRutaAndNodoIds() {
        verify(listarMicroleccionesUseCase).execute(new ListarMicroleccionesRequest(RUTA_ID, NODO_ID));
    }

    private void thenBodyHasMicroleccionWithFuente(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(MICROLECCION_ID.toString()))
                .andExpect(jsonPath("$[0].rutaId").value(RUTA_ID.toString()))
                .andExpect(jsonPath("$[0].nodoId").value(NODO_ID.toString()))
                .andExpect(jsonPath("$[0].titulo").value(TITULO))
                .andExpect(jsonPath("$[0].contenido.texto").value("Hola"))
                .andExpect(jsonPath("$[0].fuentes[0].id").value(FUENTE_ID.toString()))
                .andExpect(jsonPath("$[0].fuentes[0].titulo").value(FUENTE_TITULO))
                .andExpect(jsonPath("$[0].fuentes[0].url").value(FUENTE_URL))
                .andExpect(jsonPath("$[0].fuentes[0].permiteUsoComercial").value(true));
    }

    private void thenBadRequestWithoutUseCase(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(listarMicroleccionesUseCase);
    }

    private void thenNotFoundWithMessage(ResultActions result) throws Exception {
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ERROR_MESSAGE));
    }

    // --- helpers ---
    private MicroleccionItem microleccionItem() {
        return new MicroleccionItem(
                MICROLECCION_ID,
                RUTA_ID,
                NODO_ID,
                TITULO,
                (short) 1,
                Map.of("texto", "Hola"),
                "HUMANO",
                OffsetDateTime.parse("2026-10-01T10:00:00Z"),
                List.of(new FuenteMicroleccionItem(
                        FUENTE_ID,
                        FUENTE_TITULO,
                        FUENTE_URL,
                        "Licencia de prueba",
                        "ADAPTABLE",
                        true,
                        "Sección 1")));
    }
}
