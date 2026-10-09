package com.xpedia.backend.infrastructure.presentation.mapper.ruta;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaRequest;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaResponse;
import com.xpedia.backend.domain.dto.ruta.RutaItem;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.ListarRutasQuery;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.RutaPageResponse;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.RutaResponse;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.RutaResumenResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RutaPresentationMapper {

    private static final int PAGINA_POR_DEFECTO = 0;
    private static final int TAMANIO_POR_DEFECTO = 20;

    public ListarRutasRequest toListarRequest(ListarRutasQuery query) {
        int page = query.page() == null ? PAGINA_POR_DEFECTO : query.page();
        int size = query.size() == null ? TAMANIO_POR_DEFECTO : query.size();
        return new ListarRutasRequest(query.tipo(), query.objetivo(), page, size);
    }

    public ObtenerRutaRequest toObtenerRequest(UUID id) {
        return new ObtenerRutaRequest(id);
    }

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
        return new RutaPageResponse(
                response.content().stream().map(this::toResumen).toList(),
                response.pageNumber(),
                response.pageSize(),
                response.totalElements(),
                response.totalPages(),
                response.first(),
                response.last());
    }

    private RutaResumenResponse toResumen(RutaItem ruta) {
        return new RutaResumenResponse(
                ruta.id(),
                ruta.slug(),
                ruta.version(),
                ruta.titulo(),
                ruta.tipo(),
                ruta.objetivo(),
                ruta.meta(),
                ruta.pais(),
                ruta.horasEstimadas(),
                ruta.ritmoRecomendadoMin(),
                ruta.validacion());
    }
}
