package com.xpedia.backend.infrastructure.presentation.dto.inscripcion;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InscripcionResponse(
        UUID id,
        UUID rutaId,
        String rutaSlug,
        ObjetivoRuta objetivo,
        String metaPersonal,
        String estado,
        UUID hitoActualId,
        short ritmoMin,
        LocalDate fechaLlegadaEstimada,
        List<ProgresoNodoResponse> progreso) {
}

