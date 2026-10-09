package com.xpedia.backend.domain.service.reto;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.repository.reto.RetoRepository;
import com.xpedia.backend.domain.service.nodo.NodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetoService {

    private final NodoService nodoService;
    private final RetoRepository retoRepository;

    public List<Reto> listar(UUID rutaId, UUID nodoId) {
        nodoService.validarVisible(rutaId, nodoId);
        return retoRepository.findAprobadosByNodo(rutaId, nodoId);
    }

    public Reto obtener(UUID rutaId, UUID nodoId, UUID retoId) {
        nodoService.validarVisible(rutaId, nodoId);
        return retoRepository.findAprobadoById(rutaId, nodoId, retoId)
                .orElseThrow(() -> new ResourceNotFoundException("reto", "id", retoId));
    }
}
