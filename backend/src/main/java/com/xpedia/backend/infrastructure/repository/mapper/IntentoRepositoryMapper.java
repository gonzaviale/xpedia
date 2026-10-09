package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.enums.ModoIntento;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import com.xpedia.backend.infrastructure.repository.entity.IntentoEntity;
import com.xpedia.backend.infrastructure.repository.entity.RespuestaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class IntentoRepositoryMapper {

    public IntentoEntity toEntity(Intento intento) {
        return IntentoEntity.builder()
                .id(intento.getId())
                .usuarioId(intento.getUsuarioId())
                .inscripcionId(intento.getInscripcionId())
                .actividadId(intento.getActividadId())
                .modo(intento.getModo().name())
                .puntaje(intento.getPuntaje())
                .aprobado(intento.getAprobado())
                .iniciadoEn(intento.getIniciadoEn())
                .terminadoEn(intento.getTerminadoEn())
                .build();
    }

    public RespuestaEntity toEntity(Respuesta respuesta, UUID intentoId) {
        return RespuestaEntity.builder()
                .intentoId(intentoId)
                .preguntaId(respuesta.getPreguntaId())
                .elegida(respuesta.getElegida())
                .correcta(respuesta.getCorrecta())
                .confianza(respuesta.getConfianza() == null ? null : respuesta.getConfianza().name())
                .milisegundos(respuesta.getMilisegundos())
                .build();
    }

    public Intento toDomain(IntentoEntity entity, List<Respuesta> respuestas) {
        return Intento.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .inscripcionId(entity.getInscripcionId())
                .actividadId(entity.getActividadId())
                .modo(ModoIntento.valueOf(entity.getModo()))
                .puntaje(entity.getPuntaje())
                .aprobado(entity.getAprobado())
                .iniciadoEn(entity.getIniciadoEn())
                .terminadoEn(entity.getTerminadoEn())
                .respuestas(respuestas)
                .build();
    }
}
