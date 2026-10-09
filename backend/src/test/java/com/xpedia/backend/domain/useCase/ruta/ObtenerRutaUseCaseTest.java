package com.xpedia.backend.domain.useCase.ruta;

import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.service.ruta.RutaService;
import com.xpedia.backend.domain.dto.ruta.*;
import com.xpedia.backend.domain.mapper.ruta.ObtenerRutaMapper;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ObtenerRutaUseCaseTest {
    private final RutaService service = mock(RutaService.class);
    private final ObtenerRutaUseCase useCase = new ObtenerRutaUseCase(service, new ObtenerRutaMapper());
    @Test void ejecutaYConservaElContratoDeSalida() {
        var id = UUID.randomUUID(); var hito = UUID.randomUUID();
        var request = new ObtenerRutaRequest(id); var result = full(Ruta.class);
        when(service.obtener(id)).thenReturn(result);
        var response = useCase.execute(request); sameRecordFields(response, result);
        verify(service).obtener(id);
    }
    @Test void propagaErrorDeDominioSinConvertirloEnExito() {
        var id = UUID.randomUUID(); var hito = UUID.randomUUID();
        var request = new ObtenerRutaRequest(id); var error = new ResourceNotFoundException("ruta", "id", id);
        when(service.obtener(id)).thenThrow(error);
        assertThatThrownBy(() -> useCase.execute(request)).isSameAs(error);
    }
}
