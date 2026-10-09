package com.xpedia.backend.domain.service.microleccion;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.domain.repository.microleccion.MicroleccionRepository;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.domain.service.ruta.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MicroleccionService {
    private final RutaService rutaService;
    private final NodoRepository nodoRepository;
    private final MicroleccionRepository microleccionRepository;

    public List<Microleccion> listar(UUID rutaId, UUID nodoId) {
        rutaService.obtener(rutaId);
        if (!nodoRepository.existsVisibleByIdAndRutaId(nodoId, rutaId)) {
            throw new ResourceNotFoundException("nodo", "id", nodoId);
        }
        return microleccionRepository.findAprobadasByNodo(rutaId, nodoId);
    }
}
