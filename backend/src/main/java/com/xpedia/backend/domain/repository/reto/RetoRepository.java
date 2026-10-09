package com.xpedia.backend.domain.repository.reto;
import com.xpedia.backend.domain.model.reto.Reto;
import java.util.*;
public interface RetoRepository {
    List<Reto> findAprobadosByNodo(UUID rutaId, UUID nodoId);
    Optional<Reto> findAprobadoById(UUID rutaId, UUID nodoId, UUID retoId);
}
