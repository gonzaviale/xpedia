package com.xpedia.backend.domain.mapper.microleccion;

import com.xpedia.backend.domain.dto.microleccion.FuenteMicroleccionItem;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesResponse;
import com.xpedia.backend.domain.dto.microleccion.MicroleccionItem;
import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import com.xpedia.backend.domain.model.microleccion.Microleccion;

import java.util.List;

public class ListarMicroleccionesMapper {

    public ListarMicroleccionesResponse toResponse(List<Microleccion> microlecciones) {
        return new ListarMicroleccionesResponse(microlecciones.stream().map(this::toItem).toList());
    }

    private MicroleccionItem toItem(Microleccion microleccion) {
        return new MicroleccionItem(
                microleccion.getId(),
                microleccion.getRutaId(),
                microleccion.getNodoId(),
                microleccion.getTitulo(),
                microleccion.getNivel(),
                microleccion.getContenido(),
                microleccion.getOrigen(),
                microleccion.getRevisadoEn(),
                microleccion.getFuentes().stream().map(this::toFuenteItem).toList());
    }

    private FuenteMicroleccionItem toFuenteItem(FuenteMicroleccion fuente) {
        return new FuenteMicroleccionItem(
                fuente.id(),
                fuente.titulo(),
                fuente.url(),
                fuente.licencia(),
                fuente.uso(),
                fuente.permiteUsoComercial(),
                fuente.ubicacion());
    }
}
