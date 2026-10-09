package com.xpedia.backend.domain.mapper.intento;

import com.xpedia.backend.domain.dto.intento.CorreccionItem;
import com.xpedia.backend.domain.dto.intento.RegistrarIntentoResponse;
import com.xpedia.backend.domain.dto.intento.RespuestaEnviadaItem;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;

import java.util.List;

public class RegistrarIntentoMapper {

    public List<Respuesta> toRespuestas(List<RespuestaEnviadaItem> items) {
        return items.stream().map(this::toRespuesta).toList();
    }

    public RegistrarIntentoResponse toResponse(Intento intento) {
        return new RegistrarIntentoResponse(
                intento.getId(),
                intento.getActividadId(),
                intento.getPuntaje(),
                intento.getAprobado(),
                intento.cantidadCorrectas(),
                intento.total(),
                intento.getTerminadoEn(),
                intento.getRespuestas().stream().map(this::toCorreccionItem).toList());
    }

    private Respuesta toRespuesta(RespuestaEnviadaItem item) {
        return Respuesta.builder()
                .preguntaId(item.preguntaId())
                .elegida(item.elegida())
                .confianza(item.confianza())
                .milisegundos(item.milisegundos())
                .build();
    }

    private CorreccionItem toCorreccionItem(Respuesta respuesta) {
        return new CorreccionItem(
                respuesta.getPreguntaId(),
                respuesta.getElegida(),
                respuesta.getCorrecta(),
                respuesta.getOpcionCorrecta(),
                respuesta.getConfianza(),
                respuesta.getExplicacion());
    }
}
