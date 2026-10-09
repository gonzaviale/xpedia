package com.xpedia.backend.domain.useCase.nodo;

import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.service.nodo.NodoService;
import com.xpedia.backend.domain.dto.nodo.*;
import com.xpedia.backend.domain.mapper.nodo.ListarNodosMapper;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ListarNodosUseCaseTest {
    private final NodoService service = mock(NodoService.class);
    private final ListarNodosUseCase useCase = new ListarNodosUseCase(service, new ListarNodosMapper());
    @Test void ejecutaYConservaElContratoDeSalida() {
        var id = UUID.randomUUID(); var hito = UUID.randomUUID();
        var request = new ListarNodosRequest(id, hito); var result = List.of(full(Nodo.class));
        when(service.listar(id, hito)).thenReturn(result);
        var response = useCase.execute(request); sameRecordFields(response.content().getFirst(), result.getFirst());
        verify(service).listar(id, hito);
    }
    @Test void propagaErrorDeDominioSinConvertirloEnExito() {
        var id = UUID.randomUUID(); var hito = UUID.randomUUID();
        var request = new ListarNodosRequest(id, hito); var error = new ResourceNotFoundException("ruta", "id", id);
        when(service.listar(id, hito)).thenThrow(error);
        assertThatThrownBy(() -> useCase.execute(request)).isSameAs(error);
    }
}
