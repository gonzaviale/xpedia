package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.inscripcion.Inscripcion;
import com.xpedia.backend.infrastructure.repository.entity.InscripcionEntity;
import org.springframework.stereotype.Component;

@Component
public class InscripcionRepositoryMapper {

    public Inscripcion toDomain(InscripcionEntity entity) {
        return Inscripcion.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .rutaId(entity.getRutaId())
                .objetivo(entity.getObjetivo())
                .metaPersonal(entity.getMetaPersonal())
                .ritmoMin(entity.getRitmoMin())
                .fechaLlegadaEstimada(entity.getFechaLlegadaEstimada())
                .estado(entity.getEstado())
                .hitoActualId(entity.getHitoActualId())
                .iniciadaEn(entity.getIniciadaEn())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }

    public InscripcionEntity toEntity(Inscripcion model) {
        return InscripcionEntity.builder()
                .id(model.getId())
                .usuarioId(model.getUsuarioId())
                .rutaId(model.getRutaId())
                .objetivo(model.getObjetivo())
                .metaPersonal(model.getMetaPersonal())
                .ritmoMin(model.getRitmoMin())
                .fechaLlegadaEstimada(model.getFechaLlegadaEstimada())
                .estado(model.getEstado())
                .hitoActualId(model.getHitoActualId())
                .iniciadaEn(model.getIniciadaEn())
                .creadoEn(model.getCreadoEn())
                .actualizadoEn(model.getActualizadoEn())
                .build();
    }
}

