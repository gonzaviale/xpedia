package com.xpedia.backend.infrastructure.presentation.mapper.intento;

import com.xpedia.backend.domain.dto.intento.CorreccionItem;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoRequest;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoResponse;
import com.xpedia.backend.domain.dto.intento.RespuestaEnviadaItem;
import com.xpedia.backend.infrastructure.presentation.dto.intento.CorreccionResponse;
import com.xpedia.backend.infrastructure.presentation.dto.intento.IntentoRequest;
import com.xpedia.backend.infrastructure.presentation.dto.intento.IntentoResponse;
import com.xpedia.backend.infrastructure.presentation.dto.intento.RespuestaRequest;
import org.springframework.stereotype.Component;

@Component
public class IntentoPresentationMapper {

    public RegistrarIntentoRequest toRequest(IntentoRequest request) {
        return new RegistrarIntentoRequest(
                request.usuarioId(),
                request.inscripcionId(),
                request.actividadId(),
                request.iniciadoEn(),
                request.respuestas().stream().map(this::toRespuestaEnviadaItem).toList());
    }

    public IntentoResponse toResponse(RegistrarIntentoResponse response) {
        return new IntentoResponse(
                response.id(),
                response.actividadId(),
                response.puntaje(),
                response.aprobado(),
                response.correctas(),
                response.total(),
                response.terminadoEn(),
                response.correcciones().stream().map(this::toCorreccionResponse).toList());
    }

    private RespuestaEnviadaItem toRespuestaEnviadaItem(RespuestaRequest respuesta) {
        return new RespuestaEnviadaItem(
                respuesta.preguntaId(),
                respuesta.elegida(),
                respuesta.confianza(),
                respuesta.milisegundos());
    }

    private CorreccionResponse toCorreccionResponse(CorreccionItem correccion) {
        return new CorreccionResponse(
                correccion.preguntaId(),
                correccion.elegida(),
                correccion.correcta(),
                correccion.opcionCorrecta(),
                correccion.confianza(),
                correccion.explicacion());
    }
}
