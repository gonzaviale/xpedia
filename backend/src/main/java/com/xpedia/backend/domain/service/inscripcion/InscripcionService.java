package com.xpedia.backend.domain.service.inscripcion;

import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.model.inscripcion.Inscripcion;
import com.xpedia.backend.domain.model.inscripcion.ProgresoNodo;
import com.xpedia.backend.domain.model.inscripcion.RecorridoPersonal;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.repository.inscripcion.InscripcionRepository;
import com.xpedia.backend.domain.repository.inscripcion.ProgresoNodoRepository;
import com.xpedia.backend.domain.service.hito.HitoService;
import com.xpedia.backend.domain.service.nodo.NodoService;
import com.xpedia.backend.domain.service.ruta.RutaService;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;

    private final ProgresoNodoRepository progresoNodoRepository;

    private final UsuarioService usuarioService;

    private final RutaService rutaService;

    private final HitoService hitoService;

    private final NodoService nodoService;

    @Transactional
    public RecorridoPersonal crear(UUID usuarioId, UUID rutaId, ObjetivoRuta objetivo, String metaPersonal,
                                   short ritmoMin, OffsetDateTime fecha) {
        usuarioService.obtenerActivo(usuarioId);
        Ruta ruta = rutaService.obtener(rutaId);
        validarSinInscripcionAbierta(usuarioId, rutaId);
        List<Hito> hitos = hitoService.listar(rutaId);
        List<Nodo> nodos = nodoService.listar(rutaId, null);
        UUID hitoInicial = obtenerHitoInicial(hitos, nodos);
        Inscripcion inscripcion = Inscripcion.builder()
                .id(UUID.randomUUID())
                .usuarioId(usuarioId)
                .rutaId(rutaId)
                .objetivo(objetivo)
                .metaPersonal(Inscripcion.normalizarMeta(metaPersonal))
                .ritmoMin(ritmoMin)
                .fechaLlegadaEstimada(Inscripcion.estimarLlegada(
                        ruta.getHorasEstimadas(), ritmoMin, fecha.toLocalDate()))
                .estado("ACTIVA")
                .hitoActualId(hitoInicial)
                .iniciadaEn(fecha)
                .creadoEn(fecha)
                .actualizadoEn(fecha)
                .build();
        Inscripcion guardada = inscripcionRepository.save(inscripcion);
        List<ProgresoNodo> progreso = nodos.stream()
                .map(nodo -> ProgresoNodo.inicial(
                        guardada.getId(), nodo.getId(), !nodo.getPrerrequisitoIds().isEmpty(), fecha))
                .toList();
        progresoNodoRepository.saveAll(progreso);
        return new RecorridoPersonal(guardada, ruta.getSlug(), progreso);
    }

    public RecorridoPersonal obtenerActual(UUID usuarioId) {
        usuarioService.obtenerActivo(usuarioId);
        Inscripcion inscripcion = inscripcionRepository.findActualByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("inscripción activa", "usuario", usuarioId));
        Ruta ruta = rutaService.obtener(inscripcion.getRutaId());
        List<ProgresoNodo> progreso = progresoNodoRepository.findByInscripcionId(inscripcion.getId());
        return new RecorridoPersonal(inscripcion, ruta.getSlug(), progreso);
    }

    private void validarSinInscripcionAbierta(UUID usuarioId, UUID rutaId) {
        if (inscripcionRepository.existsAbiertaByUsuarioIdAndRutaId(usuarioId, rutaId)) {
            throw new DuplicateResourceException("inscripción abierta", "ruta", rutaId);
        }
    }

    private UUID obtenerHitoInicial(List<Hito> hitos, List<Nodo> nodos) {
        return hitos.stream()
                .filter(hito -> nodos.stream().anyMatch(nodo ->
                        hito.getId().equals(nodo.getHitoId()) && nodo.getPrerrequisitoIds().isEmpty()))
                .map(Hito::getId)
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("La ruta no tiene un hito con nodos disponibles"));
    }
}

