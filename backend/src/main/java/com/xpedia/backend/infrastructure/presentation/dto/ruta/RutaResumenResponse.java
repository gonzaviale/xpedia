package com.xpedia.backend.infrastructure.presentation.dto.ruta;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;

import java.math.BigDecimal;
import java.util.UUID;

public record RutaResumenResponse(
        UUID id,
        String slug,
        Integer version,
        String titulo,
        TipoRuta tipo,
        ObjetivoRuta objetivo,
        String meta,
        String pais,
        BigDecimal horasEstimadas,
        Short ritmoRecomendadoMin,
        ValidacionRuta validacion
) {
}
