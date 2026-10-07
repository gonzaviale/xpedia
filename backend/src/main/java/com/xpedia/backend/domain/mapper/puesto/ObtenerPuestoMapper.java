package com.xpedia.backend.domain.mapper.puesto;

import com.xpedia.backend.domain.dto.puesto.ObtenerPuestoResponse;
import com.xpedia.backend.domain.model.puesto.Puesto;

public class ObtenerPuestoMapper {

    public ObtenerPuestoResponse toResponse(Puesto puesto) {
        return new ObtenerPuestoResponse(
                puesto.getId(),
                puesto.getOrganizacionId(),
                puesto.getNombre(),
                puesto.getCreadoEn(),
                puesto.getActualizadoEn());
    }
}
