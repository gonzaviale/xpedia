package com.xpedia.backend.infrastructure.presentation.mapper.reto;

import com.xpedia.backend.domain.dto.reto.CriterioRubricaItem;
import com.xpedia.backend.domain.dto.reto.ListarRetosRequest;
import com.xpedia.backend.domain.dto.reto.ListarRetosResponse;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoRequest;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoResponse;
import com.xpedia.backend.domain.dto.reto.RetoItem;
import com.xpedia.backend.domain.dto.reto.RubricaItem;
import com.xpedia.backend.infrastructure.presentation.dto.reto.CriterioRubricaResponse;
import com.xpedia.backend.infrastructure.presentation.dto.reto.RetoResponse;
import com.xpedia.backend.infrastructure.presentation.dto.reto.RubricaResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RetoPresentationMapper {

    public ListarRetosRequest toListarRequest(UUID rutaId, UUID nodoId) {
        return new ListarRetosRequest(rutaId, nodoId);
    }

    public ObtenerRetoRequest toObtenerRequest(UUID rutaId, UUID nodoId, UUID retoId) {
        return new ObtenerRetoRequest(rutaId, nodoId, retoId);
    }

    public List<RetoResponse> toResponse(ListarRetosResponse response) {
        return response.content().stream().map(this::toResponse).toList();
    }

    public RetoResponse toResponse(ObtenerRetoResponse response) {
        return new RetoResponse(
                response.id(),
                response.rutaId(),
                response.nodoId(),
                response.hitoId(),
                response.tipo(),
                response.titulo(),
                response.nivel(),
                response.contenido(),
                response.origen(),
                response.revisadoEn(),
                toRubricaResponse(response.rubrica()));
    }

    private RetoResponse toResponse(RetoItem reto) {
        return new RetoResponse(
                reto.id(),
                reto.rutaId(),
                reto.nodoId(),
                reto.hitoId(),
                reto.tipo(),
                reto.titulo(),
                reto.nivel(),
                reto.contenido(),
                reto.origen(),
                reto.revisadoEn(),
                toRubricaResponse(reto.rubrica()));
    }

    private RubricaResponse toRubricaResponse(RubricaItem rubrica) {
        return new RubricaResponse(
                rubrica.id(),
                rubrica.nombre(),
                rubrica.descripcion(),
                rubrica.puntajeAprobacion(),
                rubrica.puntajeMaximo(),
                rubrica.criterios().stream().map(this::toCriterioResponse).toList());
    }

    private CriterioRubricaResponse toCriterioResponse(CriterioRubricaItem criterio) {
        return new CriterioRubricaResponse(
                criterio.id(),
                criterio.posicion(),
                criterio.nombre(),
                criterio.descripcion(),
                criterio.puntajeMax(),
                criterio.peso(),
                criterio.eliminatorio());
    }
}
