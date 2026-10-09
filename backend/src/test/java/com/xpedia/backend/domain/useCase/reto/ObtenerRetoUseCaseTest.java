package com.xpedia.backend.domain.useCase.reto;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoRequest;
import com.xpedia.backend.domain.mapper.reto.RetoMapper;
import com.xpedia.backend.domain.service.reto.RetoService;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.RetoTestData.reto;
import static com.xpedia.backend.support.ContractData.sameRecordFields;
class ObtenerRetoUseCaseTest {
    private final RetoService service = mock(RetoService.class);
    private final ObtenerRetoUseCase useCase = new ObtenerRetoUseCase(service,new RetoMapper());
    @Test void transmiteIdsYConservaResultado() {
        var item = reto(); when(service.obtener(item.getRutaId(),item.getNodoId(),item.getId())).thenReturn(item);
        sameRecordFields(useCase.execute(new ObtenerRetoRequest(item.getRutaId(),item.getNodoId(),item.getId())).reto(),item);
    }
    @Test void propagaErrorDeDominio() {
        var a = UUID.randomUUID(); var b = UUID.randomUUID(); var c = UUID.randomUUID(); var error = new ResourceNotFoundException("Ausente");
        when(service.obtener(a,b,c)).thenThrow(error);
        assertThatThrownBy(() -> useCase.execute(new ObtenerRetoRequest(a,b,c))).isSameAs(error);
    }
}
