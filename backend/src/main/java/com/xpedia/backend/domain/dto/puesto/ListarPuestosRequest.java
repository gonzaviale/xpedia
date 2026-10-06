package com.xpedia.backend.domain.dto.puesto;

import java.util.UUID;

public record ListarPuestosRequest(UUID organizacionId, int page, int size) {
}
