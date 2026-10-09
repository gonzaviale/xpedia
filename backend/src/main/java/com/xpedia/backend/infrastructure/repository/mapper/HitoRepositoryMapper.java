package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.infrastructure.repository.entity.HitoEntity;
import org.springframework.stereotype.Component;

@Component
public class HitoRepositoryMapper {
    public Hito toDomain(HitoEntity entity) {
        return Hito.builder()
                .id(entity.getId())
                .rutaId(entity.getRutaId())
                .posicion(entity.getPosicion())
                .titulo(entity.getTitulo())
                .objetivo(entity.getObjetivo())
                .horasEstimadas(entity.getHorasEstimadas())
                .esFinal(entity.getEsFinal())
                .evidenciaEsperada(entity.getEvidenciaEsperada())
                .estadoPropuesta(entity.getEstadoPropuesta())
                .comentarioAjuste(entity.getComentarioAjuste())
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}
