package com.xpedia.backend.domain.repository.inscripcion;

import com.xpedia.backend.domain.model.inscripcion.Inscripcion;

import java.util.Optional;
import java.util.UUID;

public interface InscripcionRepository {

    boolean existsAbiertaByUsuarioIdAndRutaId(UUID usuarioId, UUID rutaId);

    Inscripcion save(Inscripcion inscripcion);

    Optional<Inscripcion> findActualByUsuarioId(UUID usuarioId);
}

