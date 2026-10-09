package com.xpedia.backend.infrastructure.presentation.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.*;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoRequest;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.xpedia.backend.support.ContractData.*;

class PuestoPresentationMapperTest {
    @Test void conservaLasEntradasDeTodosLosCasosDeUso() {
        var mapper = new PuestoPresentationMapper(); var input = full(PuestoRequest.class); var id = UUID.randomUUID();
        sameRecordFields(mapper.toCrearRequest(input), input);
        var update = mapper.toActualizarRequest(id, input); assertThat(update.id()).isEqualTo(id); assertThat(update.nombre()).isEqualTo(input.nombre());
        assertThat(mapper.toEliminarRequest(id).id()).isEqualTo(id); assertThat(mapper.toObtenerRequest(id).id()).isEqualTo(id);
        var query = mapper.toListarRequest(id, 2, 5); assertThat(query.organizacionId()).isEqualTo(id);
        assertThat(query.page()).isEqualTo(2); assertThat(query.size()).isEqualTo(5);
    }
    @Test void conservaCadaSalidaYSusMetadatos() {
        var mapper = new PuestoPresentationMapper();
        var create = full(CrearPuestoResponse.class); sameRecordFields(mapper.toResponse(create), create);
        var update = full(ActualizarPuestoResponse.class); sameRecordFields(mapper.toResponse(update), update);
        var detail = full(ObtenerPuestoResponse.class); sameRecordFields(mapper.toResponse(detail), detail);
        var item = full(PuestoItem.class); var page = new ListarPuestosResponse(List.of(item),1,5,10,2,false,true);
        var result = mapper.toPageResponse(page); sameRecordFields(result.content().getFirst(), item);
        assertThat(result).usingRecursiveComparison().ignoringFields("content").isEqualTo(page);
    }
}
