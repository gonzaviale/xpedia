package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoRequest;
import com.xpedia.backend.domain.model.puesto.Puesto;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static com.xpedia.backend.support.ContractData.*;

class ActualizarPuestoMapperTest {
    @Test void actualizacionSoloLlevaNombreYConservaSalida() {
        var request = full(ActualizarPuestoRequest.class); var mapper = new ActualizarPuestoMapper();
        var model = mapper.toModel(request);
        assertThat(model.getId()).isNull(); assertThat(model.getOrganizacionId()).isNull();
        assertThat(model.getNombre()).isEqualTo(request.nombre());
        var saved = full(Puesto.class); sameRecordFields(mapper.toResponse(saved), saved);
    }
}
