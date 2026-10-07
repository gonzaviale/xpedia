package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.ListarPuestosRequest;
import com.xpedia.backend.domain.dto.puesto.ListarPuestosResponse;
import com.xpedia.backend.domain.dto.puesto.PuestoItem;
import com.xpedia.backend.domain.model.puesto.Puesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ListarPuestosMapper {

    public Pageable toPageable(ListarPuestosRequest request) {
        return PageRequest.of(request.page(), request.size(), Sort.by("nombre"));
    }

    public ListarPuestosResponse toResponse(Page<Puesto> page) {
        return new ListarPuestosResponse(
                page.getContent().stream().map(this::toItem).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    private PuestoItem toItem(Puesto puesto) {
        return new PuestoItem(
                puesto.getId(),
                puesto.getOrganizacionId(),
                puesto.getNombre(),
                puesto.getCreadoEn(),
                puesto.getActualizadoEn());
    }
}
