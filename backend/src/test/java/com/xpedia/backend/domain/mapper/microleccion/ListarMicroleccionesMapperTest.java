package com.xpedia.backend.domain.mapper.microleccion;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static com.xpedia.backend.support.ContractData.sameRecordFields;
import static com.xpedia.backend.support.MicroleccionTestData.microleccion;

class ListarMicroleccionesMapperTest {
    @Test void conservaTodosLosCamposJsonFuentesYNulos() {
        var first = microleccion(); var second = microleccion(); second.setRevisadoEn(null);
        var result = new ListarMicroleccionesMapper().toResponse(List.of(first, second));
        assertThat(result.content()).hasSize(2); sameRecordFields(result.content().get(0), first);
        sameRecordFields(result.content().get(1), second);
    }
    @Test void listaVaciaSeConserva() {
        assertThat(new ListarMicroleccionesMapper().toResponse(List.of()).content()).isEmpty();
    }
}
