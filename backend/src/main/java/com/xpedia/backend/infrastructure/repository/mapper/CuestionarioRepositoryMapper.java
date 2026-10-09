package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.model.cuestionario.Pregunta;
import com.xpedia.backend.domain.model.enums.TipoPregunta;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.entity.PreguntaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CuestionarioRepositoryMapper {

    public Cuestionario toDomain(ActividadEntity entity, List<PreguntaEntity> preguntas) {
        return Cuestionario.builder()
                .id(entity.getId())
                .rutaId(entity.getRutaId())
                .nodoId(entity.getNodoId())
                .titulo(entity.getTitulo())
                .nivel(entity.getNivel())
                .preguntas(preguntas.stream().map(this::toDomain).toList())
                .build();
    }

    private Pregunta toDomain(PreguntaEntity entity) {
        return Pregunta.builder()
                .id(entity.getId())
                .posicion(entity.getPosicion())
                .tipo(TipoPregunta.valueOf(entity.getTipo()))
                .enunciado(entity.getEnunciado())
                .opciones(List.copyOf(entity.getOpciones()))
                .correcta(entity.getCorrecta())
                .explicacion(entity.getExplicacion())
                .build();
    }
}
