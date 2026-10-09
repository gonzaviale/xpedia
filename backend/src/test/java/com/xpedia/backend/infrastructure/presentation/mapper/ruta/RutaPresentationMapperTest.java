package com.xpedia.backend.infrastructure.presentation.mapper.ruta;

import com.xpedia.backend.domain.dto.ruta.*;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class RutaPresentationMapperTest {
    @Test void aplicaDefaultsYConservaFiltrosExplicitos() {
        var mapper = new RutaPresentationMapper(); var defaults = mapper.toListarRequest(new ListarRutasQuery(null,null,null,null));
        assertThat(defaults.page()).isZero(); assertThat(defaults.size()).isEqualTo(20);
        var query = full(ListarRutasQuery.class); sameRecordFields(mapper.toListarRequest(query), query);
        var id = UUID.randomUUID(); assertThat(mapper.toObtenerRequest(id).id()).isEqualTo(id);
    }
    @Test void detalleYPaginaConservanDatos() {
        var mapper = new RutaPresentationMapper(); var detail = full(ObtenerRutaResponse.class);
        sameRecordFields(mapper.toResponse(detail), detail);
        var item = full(RutaItem.class); var page = new ListarRutasResponse(List.of(item),2,3,9,3,false,true);
        var result = mapper.toPageResponse(page); sameRecordFields(result.content().getFirst(), item);
        assertThat(result).usingRecursiveComparison().ignoringFields("content").isEqualTo(page);
        assertThat(mapper.toPageResponse(new ListarRutasResponse(List.of(),0,20,0,0,true,true)).content()).isEmpty();
    }
}
