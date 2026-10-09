package com.xpedia.backend.domain.service.cuestionario;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.repository.cuestionario.CuestionarioRepository;
import com.xpedia.backend.domain.service.nodo.NodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CuestionarioService {

    private final NodoService nodoService;
    private final CuestionarioRepository cuestionarioRepository;

    public List<Cuestionario> listar(UUID rutaId, UUID nodoId) {
        nodoService.validarVisible(rutaId, nodoId);
        return cuestionarioRepository.findAprobadosByNodo(rutaId, nodoId);
    }

    public Cuestionario obtenerAprobado(UUID actividadId) {
        Cuestionario cuestionario = cuestionarioRepository.findAprobadoById(actividadId)
                .orElseThrow(() -> new ResourceNotFoundException("cuestionario", "id", actividadId));
        nodoService.validarVisible(cuestionario.getRutaId(), cuestionario.getNodoId());
        return cuestionario;
    }
}
