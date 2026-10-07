package com.xpedia.backend.domain.dto.puesto;

import java.util.UUID;

public record CrearPuestoRequest(UUID organizacionId, String nombre) {
}
