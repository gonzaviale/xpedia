package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.ListarPuestosRequest;
import com.xpedia.backend.domain.model.puesto.Puesto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static com.xpedia.backend.support.ContractData.*;

class ListarPuestosMapperTest {
    @Test void conservaOrdenPaginaCamposYMetadatos() {
        var mapper = new ListarPuestosMapper(); var request = new ListarPuestosRequest(UUID.randomUUID(), 1, 5);
        var pageable = mapper.toPageable(request);
        assertThat(pageable.getSort()).isEqualTo(Sort.by("nombre"));
        assertThat(pageable.getPageNumber()).isEqualTo(1); assertThat(pageable.getPageSize()).isEqualTo(5);
        var item = full(Puesto.class); var result = mapper.toResponse(new PageImpl<>(List.of(item), pageable, 10));
        sameRecordFields(result.content().getFirst(), item);
        assertThat(result.totalElements()).isEqualTo(10); assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.pageNumber()).isEqualTo(1); assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.first()).isFalse(); assertThat(result.last()).isTrue();
        assertThat(mapper.toResponse(Page.empty()).content()).isEmpty();
    }
}
