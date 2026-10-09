package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.intento.CorreccionItem;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoRequest;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoResponse;
import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.enums.Confianza;
import com.xpedia.backend.domain.useCase.intento.RegistrarIntentoUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.intento.IntentoPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IntentoControllerTest {

    private static final UUID INTENTO_ID = UUID.fromString("00000000-0000-0000-0000-000000000a01");
    private static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000b01");
    private static final UUID ACTIVIDAD_ID = UUID.fromString("00000000-0000-0000-0000-000000000801");
    private static final UUID PREGUNTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000901");
    private static final String URL = "/api/intentos";
    private static final String EXPLICACION = "Porque sí";
    private static final String BODY_VALIDO = body(USUARIO_ID, ACTIVIDAD_ID, respuesta(PREGUNTA_ID, 1, "DUDE"));

    private MockMvc mockMvc;
    private RegistrarIntentoUseCase registrarIntentoUseCase;

    @BeforeEach
    void setUp() {
        registrarIntentoUseCase = mock(RegistrarIntentoUseCase.class);
        IntentoController controller = new IntentoController(registrarIntentoUseCase, new IntentoPresentationMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST intentos devuelve 201 con el resultado y la corrección de cada pregunta")
    void registrarShouldReturn201WithResultAndCorrecciones() throws Exception {
        givenUseCaseRegistersIntento();

        ResultActions result = performPost(BODY_VALIDO);

        thenCreatedWithIntento(result);
    }

    @Test
    @DisplayName("POST intentos pasa los datos del body al caso de uso")
    void registrarShouldPassBodyDataToUseCase() throws Exception {
        givenUseCaseRegistersIntento();

        performPost(BODY_VALIDO);

        thenUseCaseReceivedBodyData();
    }

    @Test
    @DisplayName("POST intentos devuelve 404 cuando el cuestionario no es visible")
    void registrarShouldReturn404WhenCuestionarioIsNotVisible() throws Exception {
        when(registrarIntentoUseCase.execute(any(RegistrarIntentoRequest.class)))
                .thenThrow(new ResourceNotFoundException("cuestionario", "id", ACTIVIDAD_ID));

        ResultActions result = performPost(BODY_VALIDO);

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST intentos devuelve 400 cuando las respuestas no cumplen las reglas del cuestionario")
    void registrarShouldReturn400WhenRespuestasBreakBusinessRules() throws Exception {
        when(registrarIntentoUseCase.execute(any(RegistrarIntentoRequest.class)))
                .thenThrow(new BusinessRuleException("Hay que responder las 3 preguntas del cuestionario"));

        ResultActions result = performPost(BODY_VALIDO);

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST intentos devuelve 400 cuando falta el usuario")
    void registrarShouldReturn400WhenUsuarioIdIsMissing() throws Exception {
        ResultActions result = performPost("""
                {"actividadId": "%s", "respuestas": [%s]}
                """.formatted(ACTIVIDAD_ID, respuesta(PREGUNTA_ID, 1, "DUDE")));

        result.andExpect(status().isBadRequest());
        verifyNoInteractions(registrarIntentoUseCase);
    }

    @Test
    @DisplayName("POST intentos devuelve 400 cuando no hay respuestas")
    void registrarShouldReturn400WhenRespuestasAreEmpty() throws Exception {
        ResultActions result = performPost(body(USUARIO_ID, ACTIVIDAD_ID, ""));

        result.andExpect(status().isBadRequest());
        verifyNoInteractions(registrarIntentoUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"preguntaId\": null, \"elegida\": 1, \"confianza\": \"DUDE\"}",
            "{\"preguntaId\": \"00000000-0000-0000-0000-000000000901\", \"elegida\": null, \"confianza\": \"DUDE\"}",
            "{\"preguntaId\": \"00000000-0000-0000-0000-000000000901\", \"elegida\": -1, \"confianza\": \"DUDE\"}",
            "{\"preguntaId\": \"00000000-0000-0000-0000-000000000901\", \"elegida\": 1, \"confianza\": null}",
            "{\"preguntaId\": \"00000000-0000-0000-0000-000000000901\", \"elegida\": 1, \"confianza\": \"DUDE\", "
                    + "\"milisegundos\": -5}"
    })
    @DisplayName("POST intentos devuelve 400 cuando una respuesta tiene datos inválidos")
    void registrarShouldReturn400WhenARespuestaIsInvalid(String respuesta) throws Exception {
        ResultActions result = performPost(body(USUARIO_ID, ACTIVIDAD_ID, respuesta));

        result.andExpect(status().isBadRequest());
        verifyNoInteractions(registrarIntentoUseCase);
    }

    // --- arrange ---
    private void givenUseCaseRegistersIntento() {
        CorreccionItem correccion = new CorreccionItem(
                PREGUNTA_ID,
                (short) 1,
                false,
                (short) 0,
                Confianza.DUDE,
                EXPLICACION);
        RegistrarIntentoResponse response = new RegistrarIntentoResponse(
                INTENTO_ID,
                ACTIVIDAD_ID,
                new BigDecimal("0.00"),
                false,
                0,
                1,
                OffsetDateTime.parse("2026-10-09T15:00:00Z"),
                List.of(correccion));
        when(registrarIntentoUseCase.execute(any(RegistrarIntentoRequest.class))).thenReturn(response);
    }

    // --- helpers ---
    private static String respuesta(UUID preguntaId, int elegida, String confianza) {
        return "{\"preguntaId\": \"%s\", \"elegida\": %d, \"confianza\": \"%s\", \"milisegundos\": 1500}"
                .formatted(preguntaId, elegida, confianza);
    }

    private static String body(UUID usuarioId, UUID actividadId, String respuestas) {
        return "{\"usuarioId\": \"%s\", \"actividadId\": \"%s\", \"respuestas\": [%s]}"
                .formatted(usuarioId, actividadId, respuestas);
    }

    // --- act ---
    private ResultActions performPost(String body) throws Exception {
        return mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    // --- assert ---
    private void thenCreatedWithIntento(ResultActions result) throws Exception {
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(INTENTO_ID.toString()))
                .andExpect(jsonPath("$.actividadId").value(ACTIVIDAD_ID.toString()))
                .andExpect(jsonPath("$.puntaje").value(0.0))
                .andExpect(jsonPath("$.aprobado").value(false))
                .andExpect(jsonPath("$.correctas").value(0))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.correcciones[0].preguntaId").value(PREGUNTA_ID.toString()))
                .andExpect(jsonPath("$.correcciones[0].elegida").value(1))
                .andExpect(jsonPath("$.correcciones[0].correcta").value(false))
                .andExpect(jsonPath("$.correcciones[0].opcionCorrecta").value(0))
                .andExpect(jsonPath("$.correcciones[0].confianza").value("DUDE"))
                .andExpect(jsonPath("$.correcciones[0].explicacion").value(EXPLICACION));
    }

    private void thenUseCaseReceivedBodyData() {
        ArgumentCaptor<RegistrarIntentoRequest> captor = ArgumentCaptor.forClass(RegistrarIntentoRequest.class);
        verify(registrarIntentoUseCase).execute(captor.capture());
        RegistrarIntentoRequest request = captor.getValue();
        assertThat(request.usuarioId()).isEqualTo(USUARIO_ID);
        assertThat(request.actividadId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(request.inscripcionId()).isNull();
        assertThat(request.respuestas()).hasSize(1);
        assertThat(request.respuestas().getFirst().preguntaId()).isEqualTo(PREGUNTA_ID);
        assertThat(request.respuestas().getFirst().elegida()).isEqualTo((short) 1);
        assertThat(request.respuestas().getFirst().confianza()).isEqualTo(Confianza.DUDE);
        assertThat(request.respuestas().getFirst().milisegundos()).isEqualTo(1500);
    }
}
