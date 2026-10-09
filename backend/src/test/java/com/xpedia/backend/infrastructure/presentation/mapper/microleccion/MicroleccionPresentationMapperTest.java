package com.xpedia.backend.infrastructure.presentation.mapper.microleccion;

import com.xpedia.backend.domain.mapper.microleccion.ListarMicroleccionesMapper;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static com.xpedia.backend.support.MicroleccionTestData.microleccion;

class MicroleccionPresentationMapperTest {
    @Test void conservaEntradaYSaleConJsonYCitasCompletas() {
        var mapper = new MicroleccionPresentationMapper(); var ruta = UUID.randomUUID(); var nodo = UUID.randomUUID();
        var request = mapper.toRequest(ruta, nodo);
        assertThat(request.rutaId()).isEqualTo(ruta); assertThat(request.nodoId()).isEqualTo(nodo);
        var input = new ListarMicroleccionesMapper().toResponse(List.of(microleccion()));
        assertThat(mapper.toResponse(input).getFirst()).usingRecursiveComparison().isEqualTo(input.content().getFirst());
    }
    @Test void listaVaciaSeConserva() {
        var input = new ListarMicroleccionesMapper().toResponse(List.of());
        assertThat(new MicroleccionPresentationMapper().toResponse(input)).isEmpty();
    }
}
