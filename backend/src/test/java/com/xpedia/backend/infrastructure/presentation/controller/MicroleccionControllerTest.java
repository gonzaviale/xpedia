package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.microleccion.*;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.useCase.microleccion.ListarMicroleccionesUseCase;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import com.xpedia.backend.infrastructure.presentation.mapper.microleccion.MicroleccionPresentationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MicroleccionControllerTest {
    private final ListarMicroleccionesUseCase useCase = mock(ListarMicroleccionesUseCase.class);
    private final org.springframework.test.web.servlet.MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new MicroleccionController(useCase, new MicroleccionPresentationMapper()))
            .setControllerAdvice(new GlobalExceptionHandler()).build();
    private final String root = "/api/rutas/00000000-0000-0000-0000-000000000201/nodos/00000000-0000-0000-0000-000000000501/microlecciones";

    @Test void nodoSinMaterialDevuelve200YArrayVacio() throws Exception {
        when(useCase.execute(any())).thenReturn(new ListarMicroleccionesResponse(List.of()));
        mvc.perform(get(root)).andExpect(status().isOk()).andExpect(content().json("[]"));
        verify(useCase).execute(new ListarMicroleccionesRequest(UUID.fromString("00000000-0000-0000-0000-000000000201"), UUID.fromString("00000000-0000-0000-0000-000000000501")));
    }
    @ParameterizedTest @ValueSource(strings = {"/api/rutas/texto/nodos/00000000-0000-0000-0000-000000000501/microlecciones",
            "/api/rutas/00000000-0000-0000-0000-000000000201/nodos/texto/microlecciones"})
    void uuidInvalidoNoInvocaElCasoDeUso(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(useCase);
    }
    @Test void errorDeDominioConserva404() throws Exception {
        when(useCase.execute(any())).thenThrow(new ResourceNotFoundException("Nodo ausente"));
        mvc.perform(get(root)).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Nodo ausente"));
    }
}
