package com.xpedia.backend.domain.useCase.microleccion;

import com.xpedia.backend.domain.dto.microleccion.*;
import com.xpedia.backend.domain.mapper.microleccion.ListarMicroleccionesMapper;
import com.xpedia.backend.domain.service.microleccion.MicroleccionService;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.sameRecordFields;
import static com.xpedia.backend.support.MicroleccionTestData.microleccion;

class ListarMicroleccionesUseCaseTest {
    private final MicroleccionService service = mock(MicroleccionService.class);
    private final ListarMicroleccionesUseCase useCase = new ListarMicroleccionesUseCase(service, new ListarMicroleccionesMapper());
    @Test void conservaIdentificadoresYSalidaCompleta() {
        var ruta = UUID.randomUUID(); var nodo = UUID.randomUUID(); var item = microleccion();
        when(service.listar(ruta, nodo)).thenReturn(List.of(item));
        var response = useCase.execute(new ListarMicroleccionesRequest(ruta, nodo));
        sameRecordFields(response.content().getFirst(), item); verify(service).listar(ruta, nodo);
    }
    @Test void propagaErrorSinTransformarloEnRespuestaVacia() {
        var ruta = UUID.randomUUID(); var nodo = UUID.randomUUID(); var error = new ResourceNotFoundException("nodo", "id", nodo);
        when(service.listar(ruta, nodo)).thenThrow(error);
        assertThatThrownBy(() -> useCase.execute(new ListarMicroleccionesRequest(ruta, nodo))).isSameAs(error);
    }
}
