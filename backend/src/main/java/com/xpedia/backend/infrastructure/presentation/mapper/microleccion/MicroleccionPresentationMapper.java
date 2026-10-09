package com.xpedia.backend.infrastructure.presentation.mapper.microleccion;

import com.xpedia.backend.domain.dto.microleccion.FuenteMicroleccionItem;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesRequest;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesResponse;
import com.xpedia.backend.domain.dto.microleccion.MicroleccionItem;
import com.xpedia.backend.infrastructure.presentation.dto.microleccion.FuenteMicroleccionResponse;
import com.xpedia.backend.infrastructure.presentation.dto.microleccion.MicroleccionResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MicroleccionPresentationMapper {

    public ListarMicroleccionesRequest toRequest(UUID rutaId, UUID nodoId) {
        return new ListarMicroleccionesRequest(rutaId, nodoId);
    }

    public List<MicroleccionResponse> toResponse(ListarMicroleccionesResponse response) {
        return response.content().stream().map(this::toResponse).toList();
    }

    private MicroleccionResponse toResponse(MicroleccionItem microleccion) {
        return new MicroleccionResponse(
                microleccion.id(),
                microleccion.rutaId(),
                microleccion.nodoId(),
                microleccion.titulo(),
                microleccion.nivel(),
                microleccion.contenido(),
                microleccion.origen(),
                microleccion.revisadoEn(),
                microleccion.fuentes().stream().map(this::toFuenteResponse).toList());
    }

    private FuenteMicroleccionResponse toFuenteResponse(FuenteMicroleccionItem fuente) {
        return new FuenteMicroleccionResponse(
                fuente.id(),
                fuente.titulo(),
                fuente.url(),
                fuente.licencia(),
                fuente.uso(),
                fuente.permiteUsoComercial(),
                fuente.ubicacion());
    }
}
