package com.xpedia.backend.infrastructure.presentation.dto.inscripcion;

import java.math.BigDecimal;
import java.util.UUID;

public record ProgresoNodoResponse(UUID nodoId, String estado, BigDecimal dominio) {
}

