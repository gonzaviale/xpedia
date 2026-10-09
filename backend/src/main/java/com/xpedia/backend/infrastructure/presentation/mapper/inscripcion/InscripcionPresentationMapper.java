package com.xpedia.backend.infrastructure.presentation.mapper.inscripcion;

import com.xpedia.backend.domain.dto.inscripcion.CrearInscripcionRequest;
import com.xpedia.backend.domain.dto.inscripcion.InscripcionItem;
import com.xpedia.backend.domain.dto.inscripcion.ObtenerInscripcionActualRequest;
import com.xpedia.backend.infrastructure.presentation.dto.inscripcion.CrearInscripcionWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.inscripcion.InscripcionResponse;
import com.xpedia.backend.infrastructure.presentation.dto.inscripcion.ProgresoNodoResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InscripcionPresentationMapper {

    public CrearInscripcionRequest toRequest(UUID usuarioId, CrearInscripcionWebRequest request) {
        return new CrearInscripcionRequest(
                usuarioId, request.rutaId(), request.objetivo(), request.metaPersonal(), request.ritmoMin());
    }

    public ObtenerInscripcionActualRequest toActualRequest(UUID usuarioId) {
        return new ObtenerInscripcionActualRequest(usuarioId);
    }

    public InscripcionResponse toResponse(InscripcionItem item) {
        return new InscripcionResponse(
                item.id(),
                item.rutaId(),
                item.rutaSlug(),
                item.objetivo(),
                item.metaPersonal(),
                item.estado(),
                item.hitoActualId(),
                item.ritmoMin(),
                item.fechaLlegadaEstimada(),
                item.progreso().stream()
                        .map(progreso -> new ProgresoNodoResponse(
                                progreso.nodoId(), progreso.estado(), progreso.dominio()))
                        .toList());
    }
}

