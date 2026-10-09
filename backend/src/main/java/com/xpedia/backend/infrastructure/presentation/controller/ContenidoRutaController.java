package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.useCase.hito.ListarHitosUseCase;
import com.xpedia.backend.domain.useCase.nodo.ListarNodosUseCase;
import com.xpedia.backend.infrastructure.presentation.dto.hito.HitoResponse;
import com.xpedia.backend.infrastructure.presentation.dto.nodo.NodoResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.hito.HitoPresentationMapper;
import com.xpedia.backend.infrastructure.presentation.mapper.nodo.NodoPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rutas/{rutaId}")
@Tag(name = "Rutas", description = "Catálogo público de rutas de Xpedia")
public class ContenidoRutaController {

    private final ListarHitosUseCase listarHitosUseCase;
    private final ListarNodosUseCase listarNodosUseCase;
    private final HitoPresentationMapper hitoPresentationMapper;
    private final NodoPresentationMapper nodoPresentationMapper;

    @GetMapping("/hitos")
    @Operation(summary = "Listar hitos de una ruta global publicada",
            description = "Orden por posición e ID. Ruta inexistente o fuera del catálogo: 404.")
    public ResponseEntity<List<HitoResponse>> listarHitos(@PathVariable UUID rutaId) {
        return ResponseEntity.ok(
                hitoPresentationMapper.toResponse(
                        listarHitosUseCase.execute(hitoPresentationMapper.toRequest(rutaId))));
    }

    @GetMapping("/nodos")
    @Operation(summary = "Listar nodos y sus prerrequisitos",
            description = "Filtro opcional hitoId, que debe pertenecer a la ruta. "
                    + "Los temas sin hito aparecen al final del listado general.")
    public ResponseEntity<List<NodoResponse>> listarNodos(
            @PathVariable UUID rutaId,
            @RequestParam(required = false) UUID hitoId) {
        return ResponseEntity.ok(
                nodoPresentationMapper.toResponse(
                        listarNodosUseCase.execute(nodoPresentationMapper.toRequest(rutaId, hitoId))));
    }
}
