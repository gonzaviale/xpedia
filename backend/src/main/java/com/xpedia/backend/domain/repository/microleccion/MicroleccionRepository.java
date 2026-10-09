package com.xpedia.backend.domain.repository.microleccion;

import com.xpedia.backend.domain.model.microleccion.Microleccion;

import java.util.List;
import java.util.UUID;

public interface MicroleccionRepository {

    List<Microleccion> findAprobadasByNodo(UUID rutaId, UUID nodoId);
}
