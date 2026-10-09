package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.infrastructure.repository.entity.RutaEntity;
import org.springframework.stereotype.Component;

@Component
public class RutaRepositoryMapper {

    public Ruta toDomain(RutaEntity entity) {
        return Ruta.builder()
                .id(entity.getId())
                .organizacionId(entity.getOrganizacionId())
                .slug(entity.getSlug())
                .version(entity.getVersion())
                .titulo(entity.getTitulo())
                .tipo(entity.getTipo())
                .objetivo(entity.getObjetivo())
                .meta(entity.getMeta())
                .perfilInicial(entity.getPerfilInicial())
                .pais(entity.getPais())
                .horasEstimadas(entity.getHorasEstimadas())
                .ritmoRecomendadoMin(entity.getRitmoRecomendadoMin())
                .estado(entity.getEstado())
                .validacion(entity.getValidacion())
                .revisadaPor(entity.getRevisadaPor())
                .revisadaEn(entity.getRevisadaEn())
                .confirmadaPor(entity.getConfirmadaPor())
                .confirmadaEn(entity.getConfirmadaEn())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}
