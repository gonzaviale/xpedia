package com.xpedia.backend.domain.dto.puesto;

import java.util.UUID;

public record ActualizarPuestoRequest(UUID id, String nombre) {
}
