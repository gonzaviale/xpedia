package com.xpedia.backend.domain.dto.microleccion;

import java.util.UUID;

public record FuenteMicroleccionItem(
        UUID id,
        String titulo,
        String url,
        String licencia,
        String uso,
        boolean permiteUsoComercial,
        String ubicacion
) {
}
