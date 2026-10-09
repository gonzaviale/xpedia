package com.xpedia.backend.domain.dto.inscripcion;

import java.math.BigDecimal;
import java.util.UUID;

public record ProgresoNodoItem(UUID nodoId, String estado, BigDecimal dominio) {
}

