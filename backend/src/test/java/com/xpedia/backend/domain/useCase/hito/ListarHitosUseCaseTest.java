package com.xpedia.backend.domain.useCase.hito;

import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.service.hito.HitoService;
import com.xpedia.backend.domain.dto.hito.*;
import com.xpedia.backend.domain.mapper.hito.ListarHitosMapper;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ListarHitosUseCaseTest {
    private final HitoService service = mock(HitoService.class);
    private final ListarHitosUseCase useCase = new ListarHitosUseCase(service, new ListarHitosMapper());
    @Test void ejecutaYConservaElContratoDeSalida() {
        var id = UUID.randomUUID(); var hito = UUID.randomUUID();
        var request = new ListarHitosRequest(id); var result = List.of(full(Hito.class));
        when(service.listar(id)).thenReturn(result);
        var response = useCase.execute(request); sameRecordFields(response.content().getFirst(), result.getFirst());
        verify(service).listar(id);
    }
    @Test void propagaErrorDeDominioSinConvertirloEnExito() {
        var id = UUID.randomUUID(); var hito = UUID.randomUUID();
        var request = new ListarHitosRequest(id); var error = new ResourceNotFoundException("ruta", "id", id);
        when(service.listar(id)).thenThrow(error);
        assertThatThrownBy(() -> useCase.execute(request)).isSameAs(error);
    }
}
