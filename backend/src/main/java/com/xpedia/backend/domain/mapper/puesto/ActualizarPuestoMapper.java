package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.ActualizarPuestoResponse;
import com.xpedia.backend.domain.model.puesto.Puesto;

public class ActualizarPuestoMapper {

    public Puesto toModel(ActualizarPuestoRequest request) {
        return Puesto.builder()
                .nombre(request.nombre())
                .build();
    }

    public ActualizarPuestoResponse toResponse(Puesto puesto) {
        return new ActualizarPuestoResponse(
                puesto.getId(),
                puesto.getOrganizacionId(),
                puesto.getNombre(),
                puesto.getCreadoEn(),
                puesto.getActualizadoEn());
    }
}
