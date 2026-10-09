package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import org.springframework.stereotype.Component;

@Component
public class RetoRepositoryMapper {

    public Reto toDomain(ActividadEntity entity, Rubrica rubrica) {
        return Reto.builder()
                .id(entity.getId())
                .rutaId(entity.getRutaId())
                .nodoId(entity.getNodoId())
                .hitoId(entity.getHitoId())
                .tipo(TipoReto.valueOf(entity.getTipo()))
                .titulo(entity.getTitulo())
                .nivel(entity.getNivel())
                .contenido(Reto.filtrarContenidoPublico(entity.getContenido()))
                .origen(entity.getOrigen())
                .revisadoEn(entity.getRevisadoEn())
                .rubrica(rubrica)
                .build();
    }
}
