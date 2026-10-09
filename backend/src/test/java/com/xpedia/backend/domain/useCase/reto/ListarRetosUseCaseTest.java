package com.xpedia.backend.domain.useCase.reto;
import com.xpedia.backend.domain.dto.reto.ListarRetosRequest;
import com.xpedia.backend.domain.mapper.reto.RetoMapper;
import com.xpedia.backend.domain.service.reto.RetoService;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.RetoTestData.reto;
import static com.xpedia.backend.support.ContractData.sameRecordFields;
class ListarRetosUseCaseTest {
    private final RetoService service = mock(RetoService.class);
    private final ListarRetosUseCase useCase = new ListarRetosUseCase(service,new RetoMapper());
    @Test void transmiteIdsYConservaResultado() {
        var item = reto(); when(service.listar(item.getRutaId(),item.getNodoId())).thenReturn(List.of(item));
        sameRecordFields(useCase.execute(new ListarRetosRequest(item.getRutaId(),item.getNodoId())).content().getFirst(),item);
    }
    @Test void propagaErrorDeDominio() {
        var a = UUID.randomUUID(); var b = UUID.randomUUID(); var error = new ResourceNotFoundException("Ausente");
        when(service.listar(a,b)).thenThrow(error);
        assertThatThrownBy(() -> useCase.execute(new ListarRetosRequest(a,b))).isSameAs(error);
    }
}
