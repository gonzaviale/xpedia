package com.xpedia.backend.domain.service.ruta;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.repository.ruta.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RutaService {
    private final RutaRepository rutaRepository;

    public Page<Ruta> listar(TipoRuta tipo, ObjetivoRuta objetivo, Pageable pageable) {
        return rutaRepository.findPublicadasGlobales(tipo, objetivo, pageable);
    }

    public Ruta obtener(UUID id) {
        return rutaRepository.findPublicadaGlobalById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ruta", "id", id));
    }
}
