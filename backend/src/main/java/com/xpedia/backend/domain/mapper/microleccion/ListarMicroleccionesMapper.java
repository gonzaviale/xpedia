package com.xpedia.backend.domain.mapper.microleccion;

import com.xpedia.backend.domain.dto.microleccion.*;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import java.util.List;

public class ListarMicroleccionesMapper {
    public ListarMicroleccionesResponse toResponse(List<Microleccion> items) {
        return new ListarMicroleccionesResponse(items.stream().map(this::toItem).toList());
    }

    private MicroleccionItem toItem(Microleccion item) {
        return new MicroleccionItem(item.getId(), item.getRutaId(), item.getNodoId(), item.getTitulo(),
                item.getNivel(), item.getContenido(), item.getOrigen(), item.getRevisadoEn(), item.getFuentes());
    }
}
