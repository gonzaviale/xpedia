package com.xpedia.backend.infrastructure.presentation.mapper.cuestionario;

import com.xpedia.backend.domain.dto.cuestionario.CuestionarioItem;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosRequest;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosResponse;
import com.xpedia.backend.domain.dto.cuestionario.PreguntaItem;
import com.xpedia.backend.infrastructure.presentation.dto.cuestionario.CuestionarioResponse;
import com.xpedia.backend.infrastructure.presentation.dto.cuestionario.PreguntaResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CuestionarioPresentationMapper {

    public ListarCuestionariosRequest toListarRequest(UUID rutaId, UUID nodoId) {
        return new ListarCuestionariosRequest(rutaId, nodoId);
    }

    public List<CuestionarioResponse> toResponse(ListarCuestionariosResponse response) {
        return response.content().stream().map(this::toResponse).toList();
    }

    private CuestionarioResponse toResponse(CuestionarioItem cuestionario) {
        return new CuestionarioResponse(
                cuestionario.id(),
                cuestionario.rutaId(),
                cuestionario.nodoId(),
                cuestionario.titulo(),
                cuestionario.nivel(),
                cuestionario.preguntas().stream().map(this::toPreguntaResponse).toList());
    }

    private PreguntaResponse toPreguntaResponse(PreguntaItem pregunta) {
        return new PreguntaResponse(
                pregunta.id(),
                pregunta.posicion(),
                pregunta.tipo(),
                pregunta.enunciado(),
                pregunta.opciones());
    }
}
