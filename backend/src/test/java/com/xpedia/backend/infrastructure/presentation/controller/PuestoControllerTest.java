package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.puesto.CrearPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoResponse;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosResponse;
import com.xpedia.backend.domain.useCase.puesto.*;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.puesto.PuestoPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PuestoControllerTest {

    private static final UUID PUESTO_ID = UUID.randomUUID();
    private static final String NOMBRE = "Escalamiento N2";

    private MockMvc mockMvc;
    private CrearPuestoUseCase crearPuestoUseCase;
    private ActualizarPuestoUseCase actualizar;
    private EliminarPuestoUseCase eliminar;
    private ObtenerPuestoUseCase obtener;
    private ListarPuestosUseCase listar;

    @BeforeEach
    void setUp() {
        crearPuestoUseCase = mock(CrearPuestoUseCase.class);
        actualizar = mock(ActualizarPuestoUseCase.class);
        eliminar = mock(EliminarPuestoUseCase.class);
        obtener = mock(ObtenerPuestoUseCase.class);
        listar = mock(ListarPuestosUseCase.class);
        PuestoController controller = new PuestoController(
                crearPuestoUseCase,
                actualizar,
                eliminar,
                obtener,
                listar,
                new PuestoPresentationMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Devuelve 201 con el puesto creado")
    void crearShouldReturn201WithCreatedPuesto() throws Exception {
        givenUseCaseCreatesPuesto();

        ResultActions result = performCrear(puestoJson(NOMBRE));

        result.andExpect(status().isCreated())
              .andExpect(jsonPath("$.id").value(PUESTO_ID.toString()))
              .andExpect(jsonPath("$.nombre").value(NOMBRE));
    }

    @Test
    @DisplayName("Devuelve 400 cuando el nombre está vacío")
    void crearShouldReturn400WhenNombreIsBlank() throws Exception {
        ResultActions result = performCrear(puestoJson(""));

        result.andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.errors.nombre").exists());
        verifyNoInteractions(crearPuestoUseCase);
    }

    // --- arrange ---
    @Test
    void obtenerActualizarListarYEliminarConservanEstadosYSusContratos() throws Exception {
        when(obtener.execute(any())).thenReturn(new ObtenerPuestoResponse(PUESTO_ID, null, NOMBRE, null, null));
        when(actualizar.execute(any())).thenReturn(new ActualizarPuestoResponse(PUESTO_ID, null, "Nuevo", null, null));
        when(listar.execute(any())).thenReturn(new ListarPuestosResponse(java.util.List.of(),0,20,0,0,true,true));
        mockMvc.perform(get("/api/puestos/" + PUESTO_ID)).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value(NOMBRE));
        mockMvc.perform(put("/api/puestos/" + PUESTO_ID).contentType(MediaType.APPLICATION_JSON).content(puestoJson("Nuevo")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Nuevo"));
        mockMvc.perform(get("/api/puestos")).andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty());
        mockMvc.perform(delete("/api/puestos/" + PUESTO_ID)).andExpect(status().isNoContent());
        verify(eliminar).execute(new com.xpedia.backend.domain.dto.puesto.EliminarPuestoRequest(PUESTO_ID));
    }

    @Test
    void actualizarInvalidoNoInvocaCasoDeUso() throws Exception {
        mockMvc.perform(put("/api/puestos/" + PUESTO_ID).contentType(MediaType.APPLICATION_JSON).content(puestoJson("")))
                .andExpect(status().isBadRequest()); verifyNoInteractions(actualizar);
    }

    private void givenUseCaseCreatesPuesto() {
        when(crearPuestoUseCase.execute(any(CrearPuestoRequest.class)))
                .thenReturn(new CrearPuestoResponse(PUESTO_ID, null, NOMBRE, null, null));
    }

    private String puestoJson(String nombre) {
        return "{\"nombre\":\"" + nombre + "\"}";
    }

    // --- act ---
    private ResultActions performCrear(String body) throws Exception {
        return mockMvc.perform(post("/api/puestos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}
