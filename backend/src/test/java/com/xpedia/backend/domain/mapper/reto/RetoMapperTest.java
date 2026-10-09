package com.xpedia.backend.domain.mapper.reto;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static com.xpedia.backend.support.RetoTestData.reto;
import static com.xpedia.backend.support.ContractData.sameRecordFields;
class RetoMapperTest {
    @Test void listaYDetalleConservanCadaCampoYNulos() {
        var a = reto(); var b = reto(); b.setHitoId(null); b.setRevisadoEn(null);
        var mapper = new RetoMapper(); var result = mapper.toListResponse(List.of(a,b));
        sameRecordFields(result.content().get(0),a); sameRecordFields(result.content().get(1),b);
        sameRecordFields(mapper.toObtenerResponse(a).reto(),a);
    }
    @Test void listaVaciaSeConserva() { assertThat(new RetoMapper().toListResponse(List.of()).content()).isEmpty(); }
}
