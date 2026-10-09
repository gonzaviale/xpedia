package com.xpedia.backend.domain.mapper.inscripcion;

import com.xpedia.backend.domain.dto.inscripcion.InscripcionItem;
import com.xpedia.backend.domain.dto.inscripcion.ProgresoNodoItem;
import com.xpedia.backend.domain.model.inscripcion.Inscripcion;
import com.xpedia.backend.domain.model.inscripcion.RecorridoPersonal;

public class CrearInscripcionMapper {

    public InscripcionItem toResponse(RecorridoPersonal recorrido) {
        Inscripcion inscripcion = recorrido.inscripcion();
        return new InscripcionItem(
                inscripcion.getId(),
                inscripcion.getRutaId(),
                recorrido.rutaSlug(),
                inscripcion.getObjetivo(),
                inscripcion.getMetaPersonal(),
                inscripcion.getEstado(),
                inscripcion.getHitoActualId(),
                inscripcion.getRitmoMin(),
                inscripcion.getFechaLlegadaEstimada(),
                recorrido.progreso().stream()
                        .map(progreso -> new ProgresoNodoItem(
                                progreso.getNodoId(), progreso.getEstado(), progreso.getDominio()))
                        .toList());
    }
}

