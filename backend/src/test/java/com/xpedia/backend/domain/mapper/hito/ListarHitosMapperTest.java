package com.xpedia.backend.domain.mapper.hito;

import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.dto.hito.*;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ListarHitosMapperTest {
    @Test void conservaOrdenYCadaCampoPublico() {
        var first = full(Hito.class); var second = new Hito();
        var result = new ListarHitosMapper().toResponse(List.of(first, second));
        assertThat(result.content()).hasSize(2); sameRecordFields(result.content().get(0), first); sameRecordFields(result.content().get(1), second);
    }
    @Test void coleccionVaciaDevuelveListaVacia() { assertThat(new ListarHitosMapper().toResponse(List.of()).content()).isEmpty(); }
}
