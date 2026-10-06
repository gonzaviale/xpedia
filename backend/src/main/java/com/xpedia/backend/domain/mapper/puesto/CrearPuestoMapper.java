package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.CrearPuestoRequest;
import com.xpedia.backend.domain.dto.puesto.CrearPuestoResponse;
import com.xpedia.backend.domain.model.puesto.Puesto;

public class CrearPuestoMapper {

    public Puesto toModel(CrearPuestoRequest request) {
        return Puesto.builder()
                .organizacionId(request.organizacionId())
                .nombre(request.nombre())
                .build();
    }

    public CrearPuestoResponse toResponse(Puesto puesto) {
        return new CrearPuestoResponse(
                puesto.getId(),
                puesto.getOrganizacionId(),
                puesto.getNombre(),
                puesto.getCreadoEn(),
                puesto.getActualizadoEn());
    }
}
