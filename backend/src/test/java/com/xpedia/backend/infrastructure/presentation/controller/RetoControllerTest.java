package com.xpedia.backend.infrastructure.presentation.controller;
import com.xpedia.backend.domain.mapper.reto.RetoMapper;
import com.xpedia.backend.domain.useCase.reto.*;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.infrastructure.presentation.mapper.reto.RetoPresentationMapper;
import com.xpedia.backend.infrastructure.presentation.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.xpedia.backend.support.RetoTestData.reto;
class RetoControllerTest {
    private final ListarRetosUseCase listar = mock(ListarRetosUseCase.class);
    private final ObtenerRetoUseCase obtener = mock(ObtenerRetoUseCase.class);
    private final org.springframework.test.web.servlet.MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new RetoController(listar,obtener,new RetoPresentationMapper())).setControllerAdvice(new GlobalExceptionHandler()).build();
    private final String root = "/api/rutas/00000000-0000-0000-0000-000000000201/nodos/00000000-0000-0000-0000-000000000501/retos";
    @Test void listaVaciaYDetalleConRubricaConservanContrato() throws Exception {
        when(listar.execute(any())).thenReturn(new RetoMapper().toListResponse(List.of()));
        var item = reto(); when(obtener.execute(any())).thenReturn(new RetoMapper().toObtenerResponse(item));
        mvc.perform(get(root)).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(get(root + "/" + item.getId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.rubrica.puntajeMaximo").value(2.25)).andExpect(jsonPath("$.rubrica.criterios[1].eliminatorio").value(true));
    }
    @ParameterizedTest @ValueSource(strings={"/api/rutas/texto/nodos/00000000-0000-0000-0000-000000000501/retos",
            "/api/rutas/00000000-0000-0000-0000-000000000201/nodos/texto/retos",
            "/api/rutas/00000000-0000-0000-0000-000000000201/nodos/00000000-0000-0000-0000-000000000501/retos/texto"})
    void uuidInvalidoNoInvocaCasosDeUso(String path) throws Exception {
        mvc.perform(get(path)).andExpect(status().isBadRequest()); verifyNoInteractions(listar,obtener);
    }
    @Test void detalleOcultoDevuelve404() throws Exception {
        when(obtener.execute(any())).thenThrow(new ResourceNotFoundException("Oculto"));
        mvc.perform(get(root + "/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }
}
