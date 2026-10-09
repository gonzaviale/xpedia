package com.xpedia.backend.infrastructure.presentation.mapper.reto;
import com.xpedia.backend.domain.mapper.reto.RetoMapper;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.xpedia.backend.support.RetoTestData.reto;
class RetoPresentationMapperTest {
    @Test void conservaIdsCamposPublicosCriteriosYMaximoDecimal() {
        var item = reto(); var mapper = new RetoPresentationMapper();
        var listRequest = mapper.toListRequest(item.getRutaId(),item.getNodoId());
        assertThat(listRequest.rutaId()).isEqualTo(item.getRutaId()); assertThat(listRequest.nodoId()).isEqualTo(item.getNodoId());
        var detailRequest = mapper.toObtenerRequest(item.getRutaId(),item.getNodoId(),item.getId());
        assertThat(detailRequest.rutaId()).isEqualTo(item.getRutaId()); assertThat(detailRequest.nodoId()).isEqualTo(item.getNodoId());
        assertThat(detailRequest.retoId()).isEqualTo(item.getId());
        var input = new RetoMapper().toObtenerResponse(item); var result = mapper.toResponse(input);
        assertThat(result).usingRecursiveComparison().ignoringFields("rubrica").isEqualTo(item);
        assertThat(result.rubrica()).usingRecursiveComparison().ignoringFields("puntajeMaximo").isEqualTo(item.getRubrica());
        assertThat(result.rubrica().puntajeMaximo()).isEqualByComparingTo("2.25");
        assertThat(mapper.toResponse(new RetoMapper().toListResponse(List.of(item)))).containsExactly(result);
        assertThat(mapper.toResponse(new RetoMapper().toListResponse(List.of()))).isEmpty();
    }
}
