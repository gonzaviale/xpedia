package com.xpedia.backend.domain.service.reto;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.repository.reto.RetoRepository;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.domain.service.ruta.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class RetoService {
    private final RutaService rutaService;
    private final NodoRepository nodoRepository;
    private final RetoRepository retoRepository;
    public List<Reto> listar(UUID rutaId, UUID nodoId) {
        validarNodo(rutaId, nodoId);
        return retoRepository.findAprobadosByNodo(rutaId, nodoId);
    }
    public Reto obtener(UUID rutaId, UUID nodoId, UUID retoId) {
        validarNodo(rutaId, nodoId);
        return retoRepository.findAprobadoById(rutaId, nodoId, retoId)
                .orElseThrow(() -> new ResourceNotFoundException("reto", "id", retoId));
    }
    private void validarNodo(UUID rutaId, UUID nodoId) {
        rutaService.obtener(rutaId);
        if (!nodoRepository.existsVisibleByIdAndRutaId(nodoId, rutaId)) {
            throw new ResourceNotFoundException("nodo", "id", nodoId);
        }
    }
}
