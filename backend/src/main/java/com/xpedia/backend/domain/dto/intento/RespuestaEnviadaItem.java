package com.xpedia.backend.domain.dto.intento;

import com.xpedia.backend.domain.model.enums.Confianza;

import java.util.UUID;

public record RespuestaEnviadaItem(
        UUID preguntaId,
        Short elegida,
        Confianza confianza,
        Integer milisegundos
) {
}
