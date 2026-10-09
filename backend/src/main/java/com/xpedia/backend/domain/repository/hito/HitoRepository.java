package com.xpedia.backend.domain.repository.hito;

import com.xpedia.backend.domain.model.hito.Hito;
import java.util.List;
import java.util.UUID;

public interface HitoRepository {
    List<Hito> findByRutaId(UUID rutaId);
    boolean existsByIdAndRutaId(UUID id, UUID rutaId);
}
