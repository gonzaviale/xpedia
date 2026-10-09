package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.inscripcion.CrearInscripcionRequest;
import com.xpedia.backend.domain.dto.inscripcion.ObtenerInscripcionActualRequest;
import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.inscripcion.CrearInscripcionMapper;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.useCase.inscripcion.CrearInscripcionUseCase;
import com.xpedia.backend.domain.useCase.inscripcion.ObtenerInscripcionActualUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.inscripcion.InscripcionPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.xpedia.backend.support.InscripcionTestData.INSCRIPCION_ID;
import static com.xpedia.backend.support.InscripcionTestData.META;
import static com.xpedia.backend.support.InscripcionTestData.RUTA_ID;
import static com.xpedia.backend.support.InscripcionTestData.USUARIO_ID;
import static com.xpedia.backend.support.InscripcionTestData.recorrido;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InscripcionControllerTest {

    private static final String JSON = """
            {"rutaId":"00000000-0000-0000-0000-000000000201","objetivo":"CAMBIAR",
             "metaPersonal":"Cambiar a atención remota","ritmoMin":20}
            """;

    private CrearInscripcionUseCase crearInscripcionUseCase;

    private ObtenerInscripcionActualUseCase obtenerInscripcionActualUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        crearInscripcionUseCase = mock(CrearInscripcionUseCase.class);
        obtenerInscripcionActualUseCase = mock(ObtenerInscripcionActualUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new InscripcionController(
                        crearInscripcionUseCase, obtenerInscripcionActualUseCase, new InscripcionPresentationMapper()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST devuelve 201 y transmite ruta, objetivo y usuario de sesión al caso de uso")
    void crearShouldReturn201AndDelegateSessionIdentity() throws Exception {
        givenCrearShouldReturn201AndDelegateSessionIdentity();

        ResultActions result = crear(JSON);

        thenCreatedContract(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"rutaId\":\"invalido\",\"objetivo\":\"CAMBIAR\",\"ritmoMin\":20}",
            "{\"rutaId\":\"00000000-0000-0000-0000-000000000201\",\"objetivo\":\"OTRO\",\"ritmoMin\":20}",
            "{\"rutaId\":\"00000000-0000-0000-0000-000000000201\",\"objetivo\":\"CAMBIAR\",\"ritmoMin\":0}",
            "{\"rutaId\":\"00000000-0000-0000-0000-000000000201\",\"objetivo\":\"CAMBIAR\",\"ritmoMin\":1441}",
            "{\"rutaId\":\"00000000-0000-0000-0000-000000000201\",\"objetivo\":\"CAMBIAR\",\"ritmoMin\":40000}",
            "{"
    })
    @DisplayName("Rechaza JSON, ruta, objetivo o ritmo inválido sin ejecutar casos de uso")
    void crearShouldReturn400WhenBodyInvalid(String json) throws Exception {
        ResultActions result = crear(json);

        thenCrearShouldReturn400WhenBodyInvalid(result);
    }

    @Test
    @DisplayName("Ruta oculta devuelve 404")
    void crearShouldReturn404WhenRutaHidden() throws Exception {
        givenCrearShouldReturn404WhenRutaHidden();

        ResultActions result = crear(JSON);

        thenCrearShouldReturn404WhenRutaHidden(result);
    }

    @Test
    @DisplayName("Otra inscripción abierta devuelve 409")
    void crearShouldReturn409WhenAlreadyEnrolled() throws Exception {
        givenCrearShouldReturn409WhenAlreadyEnrolled();

        ResultActions result = crear(JSON);

        thenCrearShouldReturn409WhenAlreadyEnrolled(result);
    }

    @Test
    @DisplayName("Un recorrido no preparado devuelve 400")
    void crearShouldReturn400WhenRouteNotReady() throws Exception {
        givenCrearShouldReturn400WhenRouteNotReady();

        ResultActions result = crear(JSON);

        thenCrearShouldReturn400WhenRouteNotReady(result);
    }

    @Test
    @DisplayName("GET devuelve 200 con los datos de la inscripción actual y sin caché")
    void actualShouldReturn200AndDelegateSessionIdentity() throws Exception {
        givenActualShouldReturn200AndDelegateSessionIdentity();

        ResultActions result = actual();

        thenActualContract(result);
    }

    @Test
    @DisplayName("GET sin inscripción actual devuelve 404")
    void actualShouldReturn404WhenNoEnrollment() throws Exception {
        givenActualShouldReturn404WhenNoEnrollment();

        ResultActions result = actual();

        thenActualShouldReturn404WhenNoEnrollment(result);
    }

    // --- arrange ---
    private void givenCrearShouldReturn201AndDelegateSessionIdentity() {
        when(crearInscripcionUseCase.execute(any())).thenReturn(new CrearInscripcionMapper().toResponse(recorrido()));
    }

    private void givenCrearShouldReturn404WhenRutaHidden() {
        when(crearInscripcionUseCase.execute(any()))
                .thenThrow(new ResourceNotFoundException("ruta", "id", RUTA_ID));
    }

    private void givenCrearShouldReturn409WhenAlreadyEnrolled() {
        when(crearInscripcionUseCase.execute(any()))
                .thenThrow(new DuplicateResourceException("inscripción", "ruta", RUTA_ID));
    }

    private void givenCrearShouldReturn400WhenRouteNotReady() {
        when(crearInscripcionUseCase.execute(any())).thenThrow(new BusinessRuleException("Sin nodos"));
    }

    private void givenActualShouldReturn200AndDelegateSessionIdentity() {
        when(obtenerInscripcionActualUseCase.execute(any()))
                .thenReturn(new CrearInscripcionMapper().toResponse(recorrido()));
    }

    private void givenActualShouldReturn404WhenNoEnrollment() {
        when(obtenerInscripcionActualUseCase.execute(any()))
                .thenThrow(new ResourceNotFoundException("inscripción activa", "usuario", USUARIO_ID));
    }

    // --- act ---
    private ResultActions crear(String json) throws Exception {
        return mockMvc.perform(post("/api/inscripciones")
                .principal(new UsernamePasswordAuthenticationToken(USUARIO_ID.toString(), null))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions actual() throws Exception {
        return mockMvc.perform(get("/api/inscripciones/actual")
                .principal(new UsernamePasswordAuthenticationToken(USUARIO_ID.toString(), null)));
    }

    // --- assert ---
    private void thenCreatedContract(ResultActions result) throws Exception {
        result.andExpect(status().isCreated())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.id").value(INSCRIPCION_ID.toString()))
                .andExpect(jsonPath("$.rutaId").value(RUTA_ID.toString()))
                .andExpect(jsonPath("$.objetivo").value("CAMBIAR"))
                .andExpect(jsonPath("$.progreso[0].estado").value("EN_CURSO"))
                .andExpect(jsonPath("$.usuarioId").doesNotExist())
                .andExpect(jsonPath("$.diagnostico").doesNotExist());
        ArgumentCaptor<CrearInscripcionRequest> captor = ArgumentCaptor.forClass(CrearInscripcionRequest.class);
        verify(crearInscripcionUseCase).execute(captor.capture());
        assertThat(captor.getValue().usuarioId()).isEqualTo(USUARIO_ID);
        assertThat(captor.getValue().rutaId()).isEqualTo(RUTA_ID);
        assertThat(captor.getValue().objetivo()).isEqualTo(ObjetivoRuta.CAMBIAR);
        assertThat(captor.getValue().metaPersonal()).isEqualTo(META);
        assertThat(captor.getValue().ritmoMin()).isEqualTo((short) 20);
    }

    private void thenActualContract(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.id").value(INSCRIPCION_ID.toString()))
                .andExpect(jsonPath("$.ritmoMin").value(20));
        ArgumentCaptor<ObtenerInscripcionActualRequest> captor =
                ArgumentCaptor.forClass(ObtenerInscripcionActualRequest.class);
        verify(obtenerInscripcionActualUseCase).execute(captor.capture());
        assertThat(captor.getValue().usuarioId()).isEqualTo(USUARIO_ID);
    }

    private void thenCrearShouldReturn400WhenBodyInvalid(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest());
        verifyNoInteractions(crearInscripcionUseCase, obtenerInscripcionActualUseCase);
    }

    private void thenCrearShouldReturn404WhenRutaHidden(ResultActions result) throws Exception {
        result.andExpect(status().isNotFound());
    }

    private void thenCrearShouldReturn409WhenAlreadyEnrolled(ResultActions result) throws Exception {
        result.andExpect(status().isConflict());
    }

    private void thenCrearShouldReturn400WhenRouteNotReady(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest());
    }

    private void thenActualShouldReturn404WhenNoEnrollment(ResultActions result) throws Exception {
        result.andExpect(status().isNotFound());
    }
}

