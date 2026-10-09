package com.xpedia.backend.domain.dto.ruta;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;

public record ListarRutasRequest(TipoRuta tipo, ObjetivoRuta objetivo, int page, int size) {
}
