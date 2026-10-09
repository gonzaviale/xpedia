package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.model.puesto.Puesto;
import org.junit.jupiter.api.Test;
import static com.xpedia.backend.support.ContractData.*;

class ObtenerPuestoMapperTest {
    @Test void conservaCadaCampoYNulos() {
        var mapper = new ObtenerPuestoMapper(); var saved = full(Puesto.class);
        sameRecordFields(mapper.toResponse(saved), saved);
        var empty = new Puesto(); sameRecordFields(mapper.toResponse(empty), empty);
    }
}
