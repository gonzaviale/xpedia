package com.xpedia.backend.infrastructure.presentation.mapper.reto;

import com.xpedia.backend.domain.dto.reto.*;
import com.xpedia.backend.domain.model.rubrica.*;
import com.xpedia.backend.infrastructure.presentation.dto.reto.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class RetoPresentationMapper {
    public ListarRetosRequest toListRequest(UUID rutaId, UUID nodoId) { return new ListarRetosRequest(rutaId, nodoId); }
    public ObtenerRetoRequest toObtenerRequest(UUID rutaId, UUID nodoId, UUID retoId) { return new ObtenerRetoRequest(rutaId, nodoId, retoId); }
    public List<RetoResponse> toResponse(ListarRetosResponse response) { return response.content().stream().map(this::toResponse).toList(); }
    public RetoResponse toResponse(ObtenerRetoResponse response) { return toResponse(response.reto()); }
    private RetoResponse toResponse(RetoItem item) {
        return new RetoResponse(item.id(), item.rutaId(), item.nodoId(), item.hitoId(), item.tipo(), item.titulo(),
                item.nivel(), item.contenido(), item.origen(), item.revisadoEn(), toRubrica(item.rubrica()));
    }
    private RubricaResponse toRubrica(Rubrica rubrica) {
        return new RubricaResponse(rubrica.id(), rubrica.nombre(), rubrica.descripcion(), rubrica.puntajeAprobacion(),
                rubrica.puntajeMaximo(), rubrica.criterios().stream().map(this::toCriterio).toList());
    }
    private CriterioRubricaResponse toCriterio(CriterioRubrica c) {
        return new CriterioRubricaResponse(c.id(), c.posicion(), c.nombre(), c.descripcion(), c.puntajeMax(), c.peso(), c.eliminatorio());
    }
}
