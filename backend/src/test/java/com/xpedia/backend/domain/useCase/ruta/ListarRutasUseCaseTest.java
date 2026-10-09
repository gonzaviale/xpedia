package com.xpedia.backend.domain.useCase.ruta;

import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.service.ruta.RutaService;
import com.xpedia.backend.domain.dto.ruta.*;
import com.xpedia.backend.domain.mapper.ruta.ListarRutasMapper;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ListarRutasUseCaseTest {
    private final RutaService service = mock(RutaService.class);
    private final ListarRutasUseCase useCase = new ListarRutasUseCase(service, new ListarRutasMapper());
    @Test void ejecutaYConservaElContratoDeSalida() {
        var id = UUID.randomUUID(); var hito = UUID.randomUUID();
        var request = new ListarRutasRequest(null, null, 1, 5); var result = new PageImpl<Ruta>(List.of(full(Ruta.class)), PageRequest.of(1, 5), 10);
        when(service.listar(null, null, PageRequest.of(1, 5, Sort.by("titulo", "id")))).thenReturn(result);
        var response = useCase.execute(request); assertThat(response.content()).hasSize(1); assertThat(response.pageNumber()).isEqualTo(1); assertThat(response.totalElements()).isEqualTo(10);
        verify(service).listar(null, null, PageRequest.of(1, 5, Sort.by("titulo", "id")));
    }
    @Test void propagaErrorDeDominioSinConvertirloEnExito() {
        var id = UUID.randomUUID(); var hito = UUID.randomUUID();
        var request = new ListarRutasRequest(null, null, 1, 5); var error = new ResourceNotFoundException("ruta", "id", id);
        when(service.listar(null, null, PageRequest.of(1, 5, Sort.by("titulo", "id")))).thenThrow(error);
        assertThatThrownBy(() -> useCase.execute(request)).isSameAs(error);
    }
}
