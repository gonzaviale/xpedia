package com.xpedia.backend.infrastructure.presentation.dto.puesto;

import java.util.List;

public record PuestoPageResponse(
        List<PuestoResponse> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
