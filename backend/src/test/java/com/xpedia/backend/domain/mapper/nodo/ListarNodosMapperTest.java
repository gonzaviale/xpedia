package com.xpedia.backend.domain.mapper.nodo;

import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.dto.nodo.*;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ListarNodosMapperTest {
    @Test void conservaOrdenYCadaCampoPublico() {
        var first = full(Nodo.class); var second = new Nodo();
        var result = new ListarNodosMapper().toResponse(List.of(first, second));
        assertThat(result.content()).hasSize(2); sameRecordFields(result.content().get(0), first); sameRecordFields(result.content().get(1), second);
    }
    @Test void coleccionVaciaDevuelveListaVacia() { assertThat(new ListarNodosMapper().toResponse(List.of()).content()).isEmpty(); }
}
