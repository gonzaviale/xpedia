package com.xpedia.backend.domain.mapper.ruta;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaResponse;
import com.xpedia.backend.domain.model.ruta.Ruta;

public class ObtenerRutaMapper {
    public ObtenerRutaResponse toResponse(Ruta ruta) {
        return new ObtenerRutaResponse(
                ruta.getId(),
                ruta.getSlug(),
                ruta.getVersion(),
                ruta.getTitulo(),
                ruta.getTipo(),
                ruta.getObjetivo(),
                ruta.getMeta(),
                ruta.getPerfilInicial(),
                ruta.getPais(),
                ruta.getHorasEstimadas(),
                ruta.getRitmoRecomendadoMin(),
                ruta.getEstado(),
                ruta.getValidacion(),
                ruta.getRevisadaEn(),
                ruta.getCreadoEn(),
                ruta.getActualizadoEn());
    }
}
