package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ListarPuestosRequest;
import com.xpedia.backend.domain.mapper.puesto.ListarPuestosMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListarPuestosUseCaseTest {
    @Test void conservaFiltroPaginaYRespuestaVacia() {
        var service = mock(PuestoService.class); var org = UUID.randomUUID(); var page = PageRequest.of(1, 5, Sort.by("nombre"));
        when(service.listar(org, page)).thenReturn(Page.<Puesto>empty(page));
        var result = new ListarPuestosUseCase(service, new ListarPuestosMapper()).execute(new ListarPuestosRequest(org, 1, 5));
        assertThat(result.content()).isEmpty(); assertThat(result.pageNumber()).isEqualTo(1); verify(service).listar(org, page);
    }
    @Test void propagaErrorDelServicio() {
        var service = mock(PuestoService.class); var page = PageRequest.of(0, 20, Sort.by("nombre")); var error = new IllegalStateException("Fallo de consulta");
        when(service.listar(null, page)).thenThrow(error);
        assertThatThrownBy(() -> new ListarPuestosUseCase(service, new ListarPuestosMapper()).execute(new ListarPuestosRequest(null, 0, 20))).isSameAs(error);
    }
}
