package com.xpedia.backend.domain.dto.ruta;

import java.util.List;

public record ListarRutasResponse(
        List<RutaItem> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
