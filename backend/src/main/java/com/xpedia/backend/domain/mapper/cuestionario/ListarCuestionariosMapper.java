package com.xpedia.backend.domain.mapper.cuestionario;

import com.xpedia.backend.domain.dto.cuestionario.CuestionarioItem;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosResponse;
import com.xpedia.backend.domain.dto.cuestionario.PreguntaItem;
import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.model.cuestionario.Pregunta;

import java.util.List;

public class ListarCuestionariosMapper {

    public ListarCuestionariosResponse toResponse(List<Cuestionario> cuestionarios) {
        return new ListarCuestionariosResponse(cuestionarios.stream().map(this::toItem).toList());
    }

    private CuestionarioItem toItem(Cuestionario cuestionario) {
        return new CuestionarioItem(
                cuestionario.getId(),
                cuestionario.getRutaId(),
                cuestionario.getNodoId(),
                cuestionario.getTitulo(),
                cuestionario.getNivel(),
                cuestionario.getPreguntas().stream().map(this::toPreguntaItem).toList());
    }

    private PreguntaItem toPreguntaItem(Pregunta pregunta) {
        return new PreguntaItem(
                pregunta.getId(),
                pregunta.getPosicion(),
                pregunta.getTipo(),
                pregunta.getEnunciado(),
                pregunta.getOpciones());
    }
}
