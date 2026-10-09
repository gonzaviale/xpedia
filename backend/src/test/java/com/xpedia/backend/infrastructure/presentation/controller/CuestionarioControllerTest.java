package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.cuestionario.CuestionarioItem;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosRequest;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosResponse;
import com.xpedia.backend.domain.dto.cuestionario.PreguntaItem;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.enums.TipoPregunta;
import com.xpedia.backend.domain.useCase.cuestionario.ListarCuestionariosUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.cuestionario.CuestionarioPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

class CuestionarioControllerTest {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID CUESTIONARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000801");
    private static final UUID PREGUNTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000901");
    private static final String URL = "/api/rutas/" + RUTA_ID + "/nodos/" + NODO_ID + "/cuestionarios";
    private static final String TITULO = "Escucha activa";
    private static final String ENUNCIADO = "¿Qué es parafrasear?";
    private static final Short POSICION = 1;
    private static final Short NIVEL = 2;

    private MockMvc mockMvc;
    private ListarCuestionariosUseCase listarCuestionariosUseCase;

    @BeforeEach
    void setUp() {
        listarCuestionariosUseCase = mock(ListarCuestionariosUseCase.class);
        CuestionarioController controller = new CuestionarioController(
                listarCuestionariosUseCase,
                new CuestionarioPresentationMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET cuestionarios devuelve 200 con los cuestionarios y sus preguntas sin la opción correcta")
    void listarShouldReturn200WithCuestionariosWithoutCorrectOption() throws Exception {
        givenUseCaseListsCuestionarios(new ListarCuestionariosResponse(List.of(cuestionarioItem())));

        ResultActions result = performListar(URL);

        thenOkWithCuestionario(result);
    }

    @Test
    @DisplayName("GET cuestionarios pasa el id de la ruta y del nodo al caso de uso")
    void listarShouldPassRutaIdAndNodoIdToUseCase() throws Exception {
        givenUseCaseListsCuestionarios(new ListarCuestionariosResponse(List.of()));

        performListar(URL);

        thenUseCaseReceivedRutaIdAndNodoId();
    }

    @Test
    @DisplayName("GET cuestionarios devuelve 200 con un arreglo vacío cuando el nodo no tiene cuestionarios")
    void listarShouldReturn200WithEmptyArrayWhenNodoHasNoCuestionarios() throws Exception {
        givenUseCaseListsCuestionarios(new ListarCuestionariosResponse(List.of()));

        ResultActions result = performListar(URL);

        result.andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET cuestionarios devuelve 404 cuando la ruta o el nodo no son visibles")
    void listarShouldReturn404WhenRutaOrNodoIsNotVisible() throws Exception {
        when(listarCuestionariosUseCase.execute(any(ListarCuestionariosRequest.class)))
                .thenThrow(new ResourceNotFoundException("nodo", "id", NODO_ID));

        ResultActions result = performListar(URL);

        result.andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {"id-invalido", "123", "null"})
    @DisplayName("GET cuestionarios devuelve 400 cuando el id de la ruta no es un UUID")
    void listarShouldReturn400WhenRutaIdIsNotUuid(String rutaId) throws Exception {
        ResultActions result = performListar("/api/rutas/" + rutaId + "/nodos/" + NODO_ID + "/cuestionarios");

        result.andExpect(status().isBadRequest());
        verifyNoInteractions(listarCuestionariosUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {"id-invalido", "123", "null"})
    @DisplayName("GET cuestionarios devuelve 400 cuando el id del nodo no es un UUID")
    void listarShouldReturn400WhenNodoIdIsNotUuid(String nodoId) throws Exception {
        ResultActions result = performListar("/api/rutas/" + RUTA_ID + "/nodos/" + nodoId + "/cuestionarios");

        result.andExpect(status().isBadRequest());
        verifyNoInteractions(listarCuestionariosUseCase);
    }

    // --- arrange ---
    private void givenUseCaseListsCuestionarios(ListarCuestionariosResponse response) {
        when(listarCuestionariosUseCase.execute(any(ListarCuestionariosRequest.class))).thenReturn(response);
    }

    // --- helpers ---
    private CuestionarioItem cuestionarioItem() {
        PreguntaItem pregunta = new PreguntaItem(
                PREGUNTA_ID,
                POSICION,
                TipoPregunta.CONCEPTO,
                ENUNCIADO,
                List.of("Repetir igual", "Decirlo con otras palabras"));
        return new CuestionarioItem(CUESTIONARIO_ID, RUTA_ID, NODO_ID, TITULO, NIVEL, List.of(pregunta));
    }

    // --- act ---
    private ResultActions performListar(String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    // --- assert ---
    private void thenOkWithCuestionario(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(CUESTIONARIO_ID.toString()))
                .andExpect(jsonPath("$[0].rutaId").value(RUTA_ID.toString()))
                .andExpect(jsonPath("$[0].nodoId").value(NODO_ID.toString()))
                .andExpect(jsonPath("$[0].titulo").value(TITULO))
                .andExpect(jsonPath("$[0].nivel").value(NIVEL.intValue()))
                .andExpect(jsonPath("$[0].preguntas[0].id").value(PREGUNTA_ID.toString()))
                .andExpect(jsonPath("$[0].preguntas[0].posicion").value(POSICION.intValue()))
                .andExpect(jsonPath("$[0].preguntas[0].tipo").value("CONCEPTO"))
                .andExpect(jsonPath("$[0].preguntas[0].enunciado").value(ENUNCIADO))
                .andExpect(jsonPath("$[0].preguntas[0].opciones[1]").value("Decirlo con otras palabras"))
                .andExpect(jsonPath("$[0].preguntas[0].correcta").doesNotExist())
                .andExpect(jsonPath("$[0].preguntas[0].explicacion").doesNotExist());
    }

    private void thenUseCaseReceivedRutaIdAndNodoId() {
        ArgumentCaptor<ListarCuestionariosRequest> captor = ArgumentCaptor.forClass(ListarCuestionariosRequest.class);
        verify(listarCuestionariosUseCase).execute(captor.capture());
        assertThat(captor.getValue().rutaId()).isEqualTo(RUTA_ID);
        assertThat(captor.getValue().nodoId()).isEqualTo(NODO_ID);
    }
}
