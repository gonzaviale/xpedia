package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.rubrica.*;
import com.xpedia.backend.infrastructure.repository.entity.*;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class RubricaRepositoryMapper {
    public Rubrica toDomain(RubricaEntity entity, List<CriterioRubrica> criterios) {
        return new Rubrica(entity.getId(), entity.getNombre(), entity.getDescripcion(), entity.getPuntajeAprobacion(), criterios);
    }
    public CriterioRubrica toCriterio(RubricaCriterioEntity entity) {
        return new CriterioRubrica(entity.getId(), entity.getPosicion(), entity.getNombre(), entity.getDescripcion(),
                entity.getPuntajeMax(), entity.getPeso(), entity.isEliminatorio());
    }
}
