package com.xpedia.backend.domain.dto.reto;

import java.util.UUID;

public record ObtenerRetoRequest(UUID rutaId, UUID nodoId, UUID retoId) {
}
