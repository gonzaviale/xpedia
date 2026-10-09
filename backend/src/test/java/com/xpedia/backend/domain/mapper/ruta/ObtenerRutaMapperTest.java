package com.xpedia.backend.domain.mapper.ruta;

import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.dto.ruta.*;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ObtenerRutaMapperTest {
    @Test void detalleConservaCamposPublicos() { var input = full(Ruta.class); sameRecordFields(new ObtenerRutaMapper().toResponse(input), input); }
    @Test void detalleConservaCamposOpcionalesNulos() { var input = new Ruta(); sameRecordFields(new ObtenerRutaMapper().toResponse(input), input); }
}
