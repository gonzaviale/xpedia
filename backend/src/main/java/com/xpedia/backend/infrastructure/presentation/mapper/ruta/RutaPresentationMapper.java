package com.xpedia.backend.infrastructure.presentation.mapper.ruta;

import com.xpedia.backend.domain.dto.ruta.*;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.*;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class RutaPresentationMapper {
    public ListarRutasRequest toListarRequest(ListarRutasQuery query) {
        return new ListarRutasRequest(query.tipo(), query.objetivo(),
                query.page() == null ? 0 : query.page(), query.size() == null ? 20 : query.size());
    }

    public ObtenerRutaRequest toObtenerRequest(UUID id) { return new ObtenerRutaRequest(id); }

    public RutaResponse toResponse(ObtenerRutaResponse response) {
        return new RutaResponse(
                response.id(),
                response.slug(),
                response.version(),
                response.titulo(),
                response.tipo(),
                response.objetivo(),
                response.meta(),
                response.perfilInicial(),
                response.pais(),
                response.horasEstimadas(),
                response.ritmoRecomendadoMin(),
                response.estado(),
                response.validacion(),
                response.revisadaEn(),
                response.creadoEn(),
                response.actualizadoEn());
    }

    public RutaPageResponse toPageResponse(ListarRutasResponse response) {
        return new RutaPageResponse(response.content().stream().map(this::toResumen).toList(),
                response.pageNumber(), response.pageSize(), response.totalElements(),
                response.totalPages(), response.first(), response.last());
    }

    private RutaResumenResponse toResumen(RutaItem item) {
        return new RutaResumenResponse(
                item.id(),
                item.slug(),
                item.version(),
                item.titulo(),
                item.tipo(),
                item.objetivo(),
                item.meta(),
                item.pais(),
                item.horasEstimadas(),
                item.ritmoRecomendadoMin(),
                item.validacion());
    }
}
