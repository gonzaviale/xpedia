package com.xpedia.backend.infrastructure.presentation.dto.ruta;
import java.util.List;

public record RutaPageResponse(List<RutaResumenResponse> content, int pageNumber, int pageSize,
        long totalElements, int totalPages, boolean first, boolean last) {
}
