package com.xpedia.backend.domain.useCase.puesto;

import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoRequest;
import com.xpedia.backend.domain.mapper.puesto.ActualizarPuestoMapper;
import com.xpedia.backend.domain.model.puesto.Puesto;
import com.xpedia.backend.domain.service.puesto.PuestoService;
import com.xpedia.backend.domain.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ActualizarPuestoUseCaseTest {
    @Test void transmiteIdYNombreYConservaRespuesta() {
        var service = mock(PuestoService.class); var request = full(ActualizarPuestoRequest.class); var saved = full(Puesto.class);
        when(service.actualizar(eq(request.id()), any())).thenAnswer(call -> {
            Puesto changes = call.getArgument(1); assertThat(changes.getNombre()).isEqualTo(request.nombre());
            assertThat(changes.getOrganizacionId()).isNull(); return saved;
        });
        sameRecordFields(new ActualizarPuestoUseCase(service, new ActualizarPuestoMapper()).execute(request), saved);
    }
    @Test void conservaErrorDeDuplicado() {
        var service = mock(PuestoService.class); var request = full(ActualizarPuestoRequest.class); var error = new DuplicateResourceException("Duplicado");
        when(service.actualizar(eq(request.id()), any())).thenThrow(error);
        assertThatThrownBy(() -> new ActualizarPuestoUseCase(service, new ActualizarPuestoMapper()).execute(request)).isSameAs(error);
    }
}
