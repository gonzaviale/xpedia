package com.xpedia.backend.domain.dto.ruta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;

public record ListarRutasRequest(TipoRuta tipo, ObjetivoRuta objetivo, int page, int size) {
}
