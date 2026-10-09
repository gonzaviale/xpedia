package com.xpedia.backend.domain.service.hito;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.repository.hito.HitoRepository;
import com.xpedia.backend.domain.service.ruta.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitoService {
    private final HitoRepository hitoRepository;
    private final RutaService rutaService;

    public List<Hito> listar(UUID rutaId) {
        rutaService.obtener(rutaId);
        return hitoRepository.findByRutaId(rutaId);
    }

    public void validarPertenencia(UUID rutaId, UUID hitoId) {
        if (!hitoRepository.existsByIdAndRutaId(hitoId, rutaId)) {
            throw new ResourceNotFoundException("hito", "id", hitoId);
        }
    }
}
