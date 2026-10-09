package com.xpedia.backend.domain.service.nodo;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.domain.service.hito.HitoService;
import com.xpedia.backend.domain.service.ruta.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NodoService {

    private final NodoRepository nodoRepository;
    private final RutaService rutaService;
    private final HitoService hitoService;

    public List<Nodo> listar(UUID rutaId, UUID hitoId) {
        rutaService.validarVisible(rutaId);
        if (hitoId != null) {
            hitoService.validarPertenencia(rutaId, hitoId);
        }
        return nodoRepository.findVisiblesByRutaIdAndHitoId(rutaId, hitoId);
    }

    public void validarVisible(UUID rutaId, UUID nodoId) {
        rutaService.validarVisible(rutaId);
        if (!nodoRepository.existsVisibleByIdAndRutaId(nodoId, rutaId)) {
            throw new ResourceNotFoundException("nodo", "id", nodoId);
        }
    }
}
