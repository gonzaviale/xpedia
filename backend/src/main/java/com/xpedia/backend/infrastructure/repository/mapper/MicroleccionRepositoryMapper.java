package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IActividadFuenteJpaRepository.FuenteVisible;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MicroleccionRepositoryMapper {

    public Microleccion toDomain(ActividadEntity entity, List<FuenteMicroleccion> fuentes) {
        return Microleccion.builder()
                .id(entity.getId())
                .rutaId(entity.getRutaId())
                .nodoId(entity.getNodoId())
                .titulo(entity.getTitulo())
                .nivel(entity.getNivel())
                .contenido(entity.getContenido())
                .origen(entity.getOrigen())
                .revisadoEn(entity.getRevisadoEn())
                .fuentes(fuentes)
                .build();
    }

    public FuenteMicroleccion toFuente(FuenteVisible fuente) {
        return new FuenteMicroleccion(
                fuente.getId(),
                fuente.getTitulo(),
                fuente.getUrl(),
                fuente.getLicencia(),
                fuente.getUso(),
                fuente.getPermiteUsoComercial(),
                fuente.getUbicacion());
    }
}
