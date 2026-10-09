package com.xpedia.backend.domain.service.intento;

import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.model.intento.Intento;
import com.xpedia.backend.domain.model.intento.Respuesta;
import com.xpedia.backend.domain.repository.intento.IntentoRepository;
import com.xpedia.backend.domain.service.cuestionario.CuestionarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class IntentoService {

    private final CuestionarioService cuestionarioService;
    private final IntentoRepository intentoRepository;

    public Intento registrar(UUID usuarioId,
                             UUID inscripcionId,
                             UUID actividadId,
                             List<Respuesta> respuestas,
                             OffsetDateTime iniciadoEn,
                             OffsetDateTime terminadoEn) {
        Cuestionario cuestionario = cuestionarioService.obtenerAprobado(actividadId);
        cuestionario.validarRespuestas(respuestas);
        Intento intento = Intento.corregir(
                cuestionario,
                usuarioId,
                inscripcionId,
                respuestas,
                iniciadoEn,
                terminadoEn);
        return intentoRepository.save(intento);
    }
}
