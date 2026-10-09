package com.xpedia.backend.domain.mapper.ruta;

import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.dto.ruta.*;
import org.springframework.data.domain.*;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class ListarRutasMapperTest {
    @Test void paginaConservaMetadatosYCadaCampoPublico() {
        var item = full(Ruta.class); var page = new PageImpl<Ruta>(List.of(item), PageRequest.of(2, 3), 9);
        var result = new ListarRutasMapper().toResponse(page);
        sameRecordFields(result.content().getFirst(), item);
        assertThat(result.pageNumber()).isEqualTo(2); assertThat(result.pageSize()).isEqualTo(3);
        assertThat(result.totalElements()).isEqualTo(9); assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.first()).isFalse(); assertThat(result.last()).isTrue();
    }
    @Test void paginaVaciaYOrdenEstable() {
        var mapper = new ListarRutasMapper();
        assertThat(mapper.toResponse(Page.empty()).content()).isEmpty();
        var page = mapper.toPageable(new ListarRutasRequest(null, null, 2, 5));
        assertThat(page.getPageNumber()).isEqualTo(2); assertThat(page.getPageSize()).isEqualTo(5);
        assertThat(page.getSort()).isEqualTo(Sort.by("titulo", "id"));
    }
}
