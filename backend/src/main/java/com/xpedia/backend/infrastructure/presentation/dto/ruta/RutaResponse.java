package com.xpedia.backend.infrastructure.presentation.dto.ruta;

import com.xpedia.backend.domain.model.enums.EstadoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RutaResponse(
        UUID id,
        String slug,
        Integer version,
        String titulo,
        TipoRuta tipo,
        ObjetivoRuta objetivo,
        String meta,
        String perfilInicial,
        String pais,
        BigDecimal horasEstimadas,
        Short ritmoRecomendadoMin,
        EstadoRuta estado,
        ValidacionRuta validacion,
        OffsetDateTime revisadaEn,
        OffsetDateTime creadoEn,
        OffsetDateTime actualizadoEn
) {
}
