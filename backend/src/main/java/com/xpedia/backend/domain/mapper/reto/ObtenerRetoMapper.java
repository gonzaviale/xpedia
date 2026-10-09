package com.xpedia.backend.domain.mapper.reto;

import com.xpedia.backend.domain.dto.reto.CriterioRubricaItem;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoResponse;
import com.xpedia.backend.domain.dto.reto.RubricaItem;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.model.rubrica.CriterioRubrica;
import com.xpedia.backend.domain.model.rubrica.Rubrica;

public class ObtenerRetoMapper {

    public ObtenerRetoResponse toResponse(Reto reto) {
        return new ObtenerRetoResponse(
                reto.getId(),
                reto.getRutaId(),
                reto.getNodoId(),
                reto.getHitoId(),
                reto.getTipo(),
                reto.getTitulo(),
                reto.getNivel(),
                reto.getContenido(),
                reto.getOrigen(),
                reto.getRevisadoEn(),
                toRubricaItem(reto.getRubrica()));
    }

    private RubricaItem toRubricaItem(Rubrica rubrica) {
        return new RubricaItem(
                rubrica.id(),
                rubrica.nombre(),
                rubrica.descripcion(),
                rubrica.puntajeAprobacion(),
                rubrica.puntajeMaximo(),
                rubrica.criterios().stream().map(this::toCriterioItem).toList());
    }

    private CriterioRubricaItem toCriterioItem(CriterioRubrica criterio) {
        return new CriterioRubricaItem(
                criterio.id(),
                criterio.posicion(),
                criterio.nombre(),
                criterio.descripcion(),
                criterio.puntajeMax(),
                criterio.peso(),
                criterio.eliminatorio());
    }
}
