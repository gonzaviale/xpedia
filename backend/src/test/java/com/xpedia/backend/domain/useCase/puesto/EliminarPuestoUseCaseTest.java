package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.EliminarPuestoRequest;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EliminarPuestoUseCaseTest {
    @Test void eliminaElIdRecibido() {
        var service = mock(PuestoService.class); var id = UUID.randomUUID();
        new EliminarPuestoUseCase(service).execute(new EliminarPuestoRequest(id)); verify(service).eliminar(id);
    }
    @Test void propagaErrorDeAusencia() {
        var service = mock(PuestoService.class); var id = UUID.randomUUID(); var error = new ResourceNotFoundException("Ausente");
        doThrow(error).when(service).eliminar(id);
        assertThatThrownBy(() -> new EliminarPuestoUseCase(service).execute(new EliminarPuestoRequest(id))).isSameAs(error);
    }
}
