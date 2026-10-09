package com.xpedia.backend.domain.mapper.ruta;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.dto.ruta.RutaItem;
import com.xpedia.backend.domain.model.ruta.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ListarRutasMapper {

    public Pageable toPageable(ListarRutasRequest request) {
        return PageRequest.of(request.page(), request.size(), Sort.by("titulo", "id"));
    }

    public ListarRutasResponse toResponse(Page<Ruta> page) {
        return new ListarRutasResponse(
                page.getContent().stream().map(this::toItem).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    private RutaItem toItem(Ruta ruta) {
        return new RutaItem(
                ruta.getId(),
                ruta.getSlug(),
                ruta.getVersion(),
                ruta.getTitulo(),
                ruta.getTipo(),
                ruta.getObjetivo(),
                ruta.getMeta(),
                ruta.getPais(),
                ruta.getHorasEstimadas(),
                ruta.getRitmoRecomendadoMin(),
                ruta.getValidacion());
    }
}
