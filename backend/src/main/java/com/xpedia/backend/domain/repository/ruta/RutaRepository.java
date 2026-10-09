package com.xpedia.backend.domain.repository.ruta;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface RutaRepository {
    Page<Ruta> findPublicadasGlobales(TipoRuta tipo, ObjetivoRuta objetivo, Pageable pageable);
    Optional<Ruta> findPublicadaGlobalById(UUID id);
}
