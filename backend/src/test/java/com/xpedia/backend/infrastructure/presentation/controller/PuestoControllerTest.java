package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.EliminarPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosRequest;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosResponse;
import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.PuestoItem;
import com.xpedia.backend.domain.useCase.puesto.ActualizarPuestoUseCase;
import com.xpedia.backend.domain.useCase.puesto.CrearPuestoUseCase;
import com.xpedia.backend.domain.useCase.puesto.EliminarPuestoUseCase;
import com.xpedia.backend.domain.useCase.puesto.ListarPuestosUseCase;
import com.xpedia.backend.domain.useCase.puesto.ObtenerPuestoUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.puesto.PuestoPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PuestoControllerTest {

    private static final String BASE_URL = "/api/puestos";
    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String NOMBRE = "Escalamiento N2";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-10T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-02-11T11:30:00Z");
    private static final int PAGE = 2;
    private static final int SIZE = 5;

    private MockMvc mockMvc;
    private CrearPuestoUseCase crearPuestoUseCase;
    private ActualizarPuestoUseCase actualizarPuestoUseCase;
    private EliminarPuestoUseCase eliminarPuestoUseCase;
    private ObtenerPuestoUseCase obtenerPuestoUseCase;
    private ListarPuestosUseCase listarPuestosUseCase;

    @BeforeEach
    void setUp() {
        crearPuestoUseCase = mock(CrearPuestoUseCase.class);
        actualizarPuestoUseCase = mock(ActualizarPuestoUseCase.class);
        eliminarPuestoUseCase = mock(EliminarPuestoUseCase.class);
        obtenerPuestoUseCase = mock(ObtenerPuestoUseCase.class);
        listarPuestosUseCase = mock(ListarPuestosUseCase.class);
        PuestoController controller = new PuestoController(
                crearPuestoUseCase,
                actualizarPuestoUseCase,
                eliminarPuestoUseCase,
                obtenerPuestoUseCase,
                listarPuestosUseCase,
                new PuestoPresentationMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("crear devuelve 201 con el puesto creado")
    void crearShouldReturn201WithCreatedPuesto() throws Exception {
        givenUseCaseCreatesPuesto();

        ResultActions result = performCrear(puestoJson(NOMBRE));

        thenStatusIsCreatedWithPuesto(result);
    }

    @Test
    @DisplayName("crear devuelve 400 y no invoca el caso de uso cuando el nombre está vacío")
    void crearShouldReturn400WhenNombreIsBlank() throws Exception {
        ResultActions result = performCrear(puestoJson(""));

        thenStatusIsBadRequestWithNombreError(result);
        verifyNoInteractions(crearPuestoUseCase);
    }

    @Test
    @DisplayName("obtener devuelve 200 con el puesto del id")
    void obtenerShouldReturn200WithPuesto() throws Exception {
        givenUseCaseObtainsPuesto();

        ResultActions result = performObtener();

        thenStatusIsOkWithPuesto(result);
        thenObtenerUseCaseReceivedId();
    }

    @Test
    @DisplayName("listar devuelve 200 con la página de puestos")
    void listarShouldReturn200WithPage() throws Exception {
        givenUseCaseListsOnePuesto();

        ResultActions result = performListar();

        thenStatusIsOkWithPage(result);
        thenListarUseCaseReceivedQuery();
    }

    @Test
    @DisplayName("actualizar devuelve 200 con el puesto actualizado")
    void actualizarShouldReturn200WithUpdatedPuesto() throws Exception {
        givenUseCaseUpdatesPuesto();

        ResultActions result = performActualizar(puestoJson(NOMBRE));

        thenStatusIsOkWithPuesto(result);
        thenActualizarUseCaseReceivedIdAndNombre();
    }

    @Test
    @DisplayName("actualizar devuelve 400 y no invoca el caso de uso cuando el nombre está vacío")
    void actualizarShouldReturn400WhenNombreIsBlank() throws Exception {
        ResultActions result = performActualizar(puestoJson(""));

        thenStatusIsBadRequestWithNombreError(result);
        verifyNoInteractions(actualizarPuestoUseCase);
    }

    @Test
    @DisplayName("eliminar devuelve 204 y elimina el puesto del id")
    void eliminarShouldReturn204AndDeletePuesto() throws Exception {
        ResultActions result = performEliminar();

        result.andExpect(status().isNoContent());
        verify(eliminarPuestoUseCase).execute(new EliminarPuestoRequest(PUESTO_ID));
    }

    // --- arrange ---
    private void givenUseCaseCreatesPuesto() {
        when(crearPuestoUseCase.execute(any(CrearPuestoRequest.class)))
                .thenReturn(new CrearPuestoResponse(PUESTO_ID, ORGANIZACION_ID, NOMBRE, CREADO_EN, ACTUALIZADO_EN));
    }

    private void givenUseCaseObtainsPuesto() {
        when(obtenerPuestoUseCase.execute(any(ObtenerPuestoRequest.class)))
                .thenReturn(new ObtenerPuestoResponse(PUESTO_ID, ORGANIZACION_ID, NOMBRE, CREADO_EN, ACTUALIZADO_EN));
    }

    private void givenUseCaseUpdatesPuesto() {
        when(actualizarPuestoUseCase.execute(any(ActualizarPuestoRequest.class)))
                .thenReturn(new ActualizarPuestoResponse(PUESTO_ID, ORGANIZACION_ID, NOMBRE, CREADO_EN, ACTUALIZADO_EN));
    }

    private void givenUseCaseListsOnePuesto() {
        PuestoItem item = new PuestoItem(PUESTO_ID, ORGANIZACION_ID, NOMBRE, CREADO_EN, ACTUALIZADO_EN);
        when(listarPuestosUseCase.execute(any(ListarPuestosRequest.class)))
                .thenReturn(new ListarPuestosResponse(List.of(item), PAGE, SIZE, 11, 3, false, true));
    }

    // --- helpers ---
    private String puestoJson(String nombre) {
        return "{\"nombre\":\"" + nombre + "\"}";
    }

    // --- act ---
    private ResultActions performCrear(String body) throws Exception {
        return mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions performObtener() throws Exception {
        return mockMvc.perform(get(BASE_URL + "/" + PUESTO_ID));
    }

    private ResultActions performListar() throws Exception {
        return mockMvc.perform(get(BASE_URL)
                .param("organizacionId", ORGANIZACION_ID.toString())
                .param("page", String.valueOf(PAGE))
                .param("size", String.valueOf(SIZE)));
    }

    private ResultActions performActualizar(String body) throws Exception {
        return mockMvc.perform(put(BASE_URL + "/" + PUESTO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions performEliminar() throws Exception {
        return mockMvc.perform(delete(BASE_URL + "/" + PUESTO_ID));
    }

    // --- assert ---
    private void thenStatusIsCreatedWithPuesto(ResultActions result) throws Exception {
        result.andExpect(status().isCreated());
        thenBodyHasPuesto(result, "$");
    }

    private void thenStatusIsOkWithPuesto(ResultActions result) throws Exception {
        result.andExpect(status().isOk());
        thenBodyHasPuesto(result, "$");
    }

    private void thenStatusIsOkWithPage(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.pageNumber").value(PAGE))
              .andExpect(jsonPath("$.pageSize").value(SIZE))
              .andExpect(jsonPath("$.totalElements").value(11))
              .andExpect(jsonPath("$.totalPages").value(3))
              .andExpect(jsonPath("$.first").value(false))
              .andExpect(jsonPath("$.last").value(true));
        thenBodyHasPuesto(result, "$.content[0]");
    }

    private void thenBodyHasPuesto(ResultActions result, String path) throws Exception {
        result.andExpect(jsonPath(path + ".id").value(PUESTO_ID.toString()))
              .andExpect(jsonPath(path + ".organizacionId").value(ORGANIZACION_ID.toString()))
              .andExpect(jsonPath(path + ".nombre").value(NOMBRE))
              .andExpect(jsonPath(path + ".creadoEn").exists())
              .andExpect(jsonPath(path + ".actualizadoEn").exists());
    }

    private void thenStatusIsBadRequestWithNombreError(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.errors.nombre").exists());
    }

    private void thenObtenerUseCaseReceivedId() {
        verify(obtenerPuestoUseCase).execute(new ObtenerPuestoRequest(PUESTO_ID));
    }

    private void thenListarUseCaseReceivedQuery() {
        verify(listarPuestosUseCase).execute(new ListarPuestosRequest(ORGANIZACION_ID, PAGE, SIZE));
    }

    private void thenActualizarUseCaseReceivedIdAndNombre() {
        verify(actualizarPuestoUseCase).execute(new ActualizarPuestoRequest(PUESTO_ID, NOMBRE));
    }
}
