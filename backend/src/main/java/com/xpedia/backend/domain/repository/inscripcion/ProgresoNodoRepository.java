package com.xpedia.backend.domain.repository.inscripcion;

import com.xpedia.backend.domain.model.inscripcion.ProgresoNodo;

import java.util.List;
import java.util.UUID;

public interface ProgresoNodoRepository {

    void saveAll(List<ProgresoNodo> progreso);

    List<ProgresoNodo> findByInscripcionId(UUID inscripcionId);
}

