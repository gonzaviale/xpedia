package com.xpedia.backend.domain.repository.cuestionario;

import com.xpedia.backend.domain.model.cuestionario.Cuestionario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CuestionarioRepository {

    List<Cuestionario> findAprobadosByNodo(UUID rutaId, UUID nodoId);

    Optional<Cuestionario> findAprobadoById(UUID actividadId);
}
