package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.useCase.reto.*;
import com.xpedia.backend.infrastructure.presentation.dto.reto.RetoResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.reto.RetoPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/rutas/{rutaId}/nodos/{nodoId}/retos")
@Tag(name = "Retos", description = "Consignas públicas aprobadas y sus rúbricas")
public class RetoController {
    private final ListarRetosUseCase listar;
    private final ObtenerRetoUseCase obtener;
    private final RetoPresentationMapper mapper;
    @GetMapping
    @Operation(summary = "Listar retos de un nodo", description = "ENSAYO, RETO_PROYECTO y DESAFIO_REAL aprobados con rúbrica global válida. Nodo sin retos: 200 con [].")
    public ResponseEntity<List<RetoResponse>> listar(@PathVariable UUID rutaId, @PathVariable UUID nodoId) {
        return ResponseEntity.ok(mapper.toResponse(listar.execute(mapper.toListRequest(rutaId, nodoId))));
    }
    @GetMapping("/{retoId}")
    @Operation(summary = "Consultar consigna y rúbrica de un reto", description = "El reto debe pertenecer al nodo de la ruta global publicada. Oculto o inexistente: 404; UUID inválido: 400.")
    public ResponseEntity<RetoResponse> obtener(@PathVariable UUID rutaId, @PathVariable UUID nodoId, @PathVariable UUID retoId) {
        return ResponseEntity.ok(mapper.toResponse(obtener.execute(mapper.toObtenerRequest(rutaId, nodoId, retoId))));
    }
}
