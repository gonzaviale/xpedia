package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.inscripcion.ProgresoNodo;
import com.xpedia.backend.infrastructure.repository.entity.ProgresoNodoEntity;
import org.springframework.stereotype.Component;

@Component
public class ProgresoNodoRepositoryMapper {

    public ProgresoNodo toDomain(ProgresoNodoEntity entity) {
        return ProgresoNodo.builder()
                .inscripcionId(entity.getInscripcionId())
                .nodoId(entity.getNodoId())
                .estado(entity.getEstado())
                .dominio(entity.getDominio())
                .nivel(entity.getNivel())
                .cantidadFallos(entity.getCantidadFallos())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }

    public ProgresoNodoEntity toEntity(ProgresoNodo model) {
        return ProgresoNodoEntity.builder()
                .inscripcionId(model.getInscripcionId())
                .nodoId(model.getNodoId())
                .estado(model.getEstado())
                .dominio(model.getDominio())
                .nivel(model.getNivel())
                .cantidadFallos(model.getCantidadFallos())
                .creadoEn(model.getCreadoEn())
                .actualizadoEn(model.getActualizadoEn())
                .build();
    }
}

