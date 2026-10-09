package com.xpedia.backend.infrastructure.presentation.mapper.microleccion;

import com.xpedia.backend.domain.dto.microleccion.*;
import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import com.xpedia.backend.infrastructure.presentation.dto.microleccion.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MicroleccionPresentationMapper {
    public ListarMicroleccionesRequest toRequest(UUID rutaId, UUID nodoId) {
        return new ListarMicroleccionesRequest(rutaId, nodoId);
    }

    public List<MicroleccionResponse> toResponse(ListarMicroleccionesResponse response) {
        return response.content().stream().map(this::toResponse).toList();
    }

    private MicroleccionResponse toResponse(MicroleccionItem item) {
        return new MicroleccionResponse(item.id(), item.rutaId(), item.nodoId(), item.titulo(), item.nivel(),
                item.contenido(), item.origen(), item.revisadoEn(), item.fuentes().stream().map(this::toFuente).toList());
    }

    private FuenteMicroleccionResponse toFuente(FuenteMicroleccion fuente) {
        return new FuenteMicroleccionResponse(fuente.id(), fuente.titulo(), fuente.url(), fuente.licencia(),
                fuente.uso(), fuente.permiteUsoComercial(), fuente.ubicacion());
    }
}
