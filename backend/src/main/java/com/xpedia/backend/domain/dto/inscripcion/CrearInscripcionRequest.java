package com.xpedia.backend.domain.dto.inscripcion;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;

import java.util.UUID;

public record CrearInscripcionRequest(
        UUID usuarioId, UUID rutaId, ObjetivoRuta objetivo, String metaPersonal, short ritmoMin) {
}

