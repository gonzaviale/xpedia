package com.xpedia.backend.infrastructure.repository.mapper;
import com.xpedia.backend.infrastructure.repository.entity.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static com.xpedia.backend.support.ContractData.*;
class RubricaRepositoryMapperTest {
    @Test void conservaCamposYCriteriosIncluyendoNulosYPesos() {
        var entity = full(RubricaEntity.class); entity.setDescripcion(null);
        var c = full(RubricaCriterioEntity.class); var mapper = new RubricaRepositoryMapper();
        var criterion = mapper.toCriterio(c); sameRecordFields(criterion,c);
        var result = mapper.toDomain(entity,List.of(criterion));
        assertThat(result).usingRecursiveComparison().ignoringFields("criterios").isEqualTo(entity);
        assertThat(result.criterios()).containsExactly(criterion);
    }
}
