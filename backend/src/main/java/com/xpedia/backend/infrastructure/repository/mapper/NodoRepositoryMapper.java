package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.infrastructure.repository.entity.NodoEntity;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class NodoRepositoryMapper {
    public Nodo toDomain(NodoEntity entity, List<UUID> prerrequisitoIds, UUID ramaId, UUID habilidadId) {
        return Nodo.builder()
                .id(entity.getId())
                .rutaId(entity.getRutaId())
                .hitoId(entity.getHitoId())
                .ramaId(ramaId)
                .habilidadId(habilidadId)
                .codigo(entity.getCodigo())
                .titulo(entity.getTitulo())
                .resumen(entity.getResumen())
                .tipo(entity.getTipo())
                .nivel(entity.getNivel())
                .minutosEstimados(entity.getMinutosEstimados())
                .palabrasClave(Arrays.asList(entity.getPalabrasClave()))
                .posicion(entity.getPosicion())
                .prerrequisitoIds(prerrequisitoIds)
                .creadoEn(entity.getCreadoEn())
                .actualizadoEn(entity.getActualizadoEn())
                .build();
    }
}
