package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.domain.dto.nodo.ListarNodosResponse;
import com.xpedia.backend.domain.useCase.hito.ListarHitosUseCase;
import com.xpedia.backend.domain.useCase.nodo.ListarNodosUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.hito.HitoPresentationMapper;
import com.xpedia.backend.infrastructure.presentation.mapper.nodo.NodoPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ContenidoRutaControllerTest {
    private MockMvc mockMvc;
    private ListarHitosUseCase hitos;
    private ListarNodosUseCase nodos;

    @BeforeEach
    void setUp() {
        hitos = mock(ListarHitosUseCase.class);
        nodos = mock(ListarNodosUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ContenidoRutaController(
                hitos, nodos, new HitoPresentationMapper(), new NodoPresentationMapper()))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/rutas/id-invalido/hitos", "/api/rutas/id-invalido/nodos",
            "/api/rutas/00000000-0000-0000-0000-000000000201/nodos?hitoId=invalido"
    })
    void rechazaUuidInvalidoAntesDeInvocarCasosDeUso(String path) throws Exception {
        mockMvc.perform(get(path)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(hitos, nodos);
    }

    @Test
    void entregaArraysVaciosParaRutaSinContenido() throws Exception {
        when(hitos.execute(any())).thenReturn(new ListarHitosResponse(List.of()));
        when(nodos.execute(any())).thenReturn(new ListarNodosResponse(List.of()));
        String root = "/api/rutas/00000000-0000-0000-0000-000000000201";
        mockMvc.perform(get(root + "/hitos")).andExpect(status().isOk()).andExpect(content().json("[]"));
        mockMvc.perform(get(root + "/nodos")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }
}
