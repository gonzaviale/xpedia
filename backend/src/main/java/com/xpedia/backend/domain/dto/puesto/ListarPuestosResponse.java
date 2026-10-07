package com.xpedia.backend.domain.dto.puesto;

import java.util.List;

public record ListarPuestosResponse(List<PuestoItem> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean first, boolean last) {
}
