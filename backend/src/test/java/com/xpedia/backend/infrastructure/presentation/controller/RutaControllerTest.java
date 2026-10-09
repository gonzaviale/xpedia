package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.useCase.ruta.ListarRutasUseCase;
import com.xpedia.backend.domain.useCase.ruta.ObtenerRutaUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.ruta.RutaPresentationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RutaControllerTest {
    private MockMvc mockMvc;
    private ListarRutasUseCase listar;
    private ObtenerRutaUseCase obtener;

    @BeforeEach
    void setUp() {
        listar = mock(ListarRutasUseCase.class);
        obtener = mock(ObtenerRutaUseCase.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RutaController(listar, obtener, new RutaPresentationMapper()))
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void aplicaPaginacionPorDefectoYDevuelvePaginaVacia() throws Exception {
        var request = new ListarRutasRequest(null, null, 0, 20);
        when(listar.execute(request)).thenReturn(new ListarRutasResponse(List.of(), 0, 20, 0, 0, true, true));
        mockMvc.perform(get("/api/rutas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.pageSize").value(20));
        verify(listar).execute(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "page=-1", "size=0", "size=-1", "size=101", "page=texto", "size=texto",
            "page=2147483648", "tipo=INVALIDA", "objetivo=INVALIDO", "tipo=tecnica"
    })
    void rechazaFiltrosYPaginacionInvalidos(String query) throws Exception {
        mockMvc.perform(get("/api/rutas?" + query))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isNotEmpty());
        verifyNoInteractions(listar, obtener);
    }

    @Test
    void rechazaIdQueNoEsUuid() throws Exception {
        mockMvc.perform(get("/api/rutas/id-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.id").exists());
        verifyNoInteractions(listar, obtener);
    }

    @Test
    void traduceRutaNoEncontradaA404() throws Exception {
        UUID id = UUID.randomUUID();
        when(obtener.execute(any())).thenThrow(new ResourceNotFoundException("ruta", "id", id));
        mockMvc.perform(get("/api/rutas/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
