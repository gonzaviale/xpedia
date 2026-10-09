package com.xpedia.backend.infrastructure.repository.mapper;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static com.xpedia.backend.support.ContractData.full;
import static com.xpedia.backend.support.RetoTestData.rubrica;
class RetoRepositoryMapperTest {
    @ParameterizedTest @ValueSource(strings={"ENSAYO","RETO_PROYECTO","DESAFIO_REAL"})
    void conservaCamposPublicosYOcultaSolucionesYConfiguracion(String tipo) {
        var entity = full(ActividadEntity.class); entity.setTipo(tipo);
        entity.setContenido(Map.of("consigna","Texto","contexto","Situación","formatoEntrega","Escrito",
                "respuestaEsperada","SECRETO","evaluador",Map.of("clave","SECRETO")));
        var rubric = rubrica(); var result = new RetoRepositoryMapper().toDomain(entity,rubric);
        assertThat(result).usingRecursiveComparison().ignoringFields("contenido","tipo","rubrica").isEqualTo(entity);
        assertThat(result.getTipo().name()).isEqualTo(tipo); assertThat(result.getRubrica()).isSameAs(rubric);
        assertThat(result.getContenido()).containsExactlyInAnyOrderEntriesOf(Map.of("consigna","Texto","contexto","Situación","formatoEntrega","Escrito"));
    }
    @Test void camposOpcionalesAusentesONoTextualesNoSeExponen() {
        var entity = full(ActividadEntity.class); entity.setTipo("ENSAYO");
        entity.setContenido(Map.of("consigna","Texto","contexto",Map.of("secreto","dato"),"formatoEntrega",17));
        assertThat(new RetoRepositoryMapper().toDomain(entity,rubrica()).getContenido()).containsOnlyKeys("consigna");
        entity.setContenido(Map.of("consigna","Texto"));
        assertThat(new RetoRepositoryMapper().toDomain(entity,rubrica()).getContenido()).containsOnlyKeys("consigna");
    }
}
