package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.CrearPuestoRequest;
import com.xpedia.backend.domain.model.puesto.Puesto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static com.xpedia.backend.support.ContractData.*;

class CrearPuestoMapperTest {
    @Test void construyeNuevoModeloSinIdYConservaSalida() {
        var request = full(CrearPuestoRequest.class); var mapper = new CrearPuestoMapper();
        var model = mapper.toModel(request);
        assertThat(model.getId()).isNull(); assertThat(model.getNombre()).isEqualTo(request.nombre());
        assertThat(model.getOrganizacionId()).isEqualTo(request.organizacionId());
        var saved = full(Puesto.class); sameRecordFields(mapper.toResponse(saved), saved);
    }
}
