package com.xpedia.backend.infrastructure.presentation.mapper.hito;

import com.xpedia.backend.domain.dto.hito.*;
import com.xpedia.backend.infrastructure.presentation.dto.hito.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class HitoPresentationMapperTest {
    @Test void conservaIdsDeEntradaYCamposDeSalida() {
        var mapper = new HitoPresentationMapper(); var id = UUID.randomUUID(); var request = mapper.toRequest(id);
        assertThat(request.rutaId()).isEqualTo(id); var item = full(HitoItem.class);
        sameRecordFields(mapper.toResponse(new ListarHitosResponse(List.of(item))).getFirst(), item);
    }
    @Test void respuestaVaciaSeConserva() { assertThat(new HitoPresentationMapper().toResponse(new ListarHitosResponse(List.of()))).isEmpty(); }
}
