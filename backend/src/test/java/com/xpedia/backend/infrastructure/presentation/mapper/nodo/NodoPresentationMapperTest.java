package com.xpedia.backend.infrastructure.presentation.mapper.nodo;

import com.xpedia.backend.domain.dto.nodo.*;
import com.xpedia.backend.infrastructure.presentation.dto.nodo.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class NodoPresentationMapperTest {
    @Test void conservaIdsDeEntradaYCamposDeSalida() {
        var mapper = new NodoPresentationMapper(); var id = UUID.randomUUID(); var hito = UUID.randomUUID(); var request = mapper.toRequest(id, hito); assertThat(request.hitoId()).isEqualTo(hito);
        assertThat(request.rutaId()).isEqualTo(id); var item = full(NodoItem.class);
        sameRecordFields(mapper.toResponse(new ListarNodosResponse(List.of(item))).getFirst(), item);
    }
    @Test void respuestaVaciaSeConserva() { assertThat(new NodoPresentationMapper().toResponse(new ListarNodosResponse(List.of()))).isEmpty(); }
}
