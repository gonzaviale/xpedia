package com.xpedia.backend.domain.service.microleccion;

import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.domain.repository.microleccion.MicroleccionRepository;
import com.xpedia.backend.domain.service.nodo.NodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MicroleccionService {

    private final NodoService nodoService;
    private final MicroleccionRepository microleccionRepository;

    public List<Microleccion> listar(UUID rutaId, UUID nodoId) {
        nodoService.validarVisible(rutaId, nodoId);
        return microleccionRepository.findAprobadasByNodo(rutaId, nodoId);
    }
}
