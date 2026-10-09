package com.xpedia.backend.domain.repository.nodo;

import com.xpedia.backend.domain.model.nodo.Nodo;
import java.util.List;
import java.util.UUID;

public interface NodoRepository {
    boolean existsVisibleByIdAndRutaId(UUID id, UUID rutaId);
    List<Nodo> findByRutaId(UUID rutaId, UUID hitoId);
}
