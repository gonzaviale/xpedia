package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoRequest;
import com.xpedia.backend.domain.mapper.puesto.ObtenerPuestoMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ObtenerPuestoUseCaseTest {
    @Test void consultaIdYConservaSalida() {
        var service = mock(PuestoService.class); var saved = full(Puesto.class);
        when(service.obtener(saved.getId())).thenReturn(saved);
        sameRecordFields(new ObtenerPuestoUseCase(service, new ObtenerPuestoMapper()).execute(new ObtenerPuestoRequest(saved.getId())), saved);
    }
    @Test void propagaAusencia() {
        var service = mock(PuestoService.class); var id = UUID.randomUUID(); var error = new ResourceNotFoundException("Ausente");
        when(service.obtener(id)).thenThrow(error);
        assertThatThrownBy(() -> new ObtenerPuestoUseCase(service, new ObtenerPuestoMapper()).execute(new ObtenerPuestoRequest(id))).isSameAs(error);
    }
}
