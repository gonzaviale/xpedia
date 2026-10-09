package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.useCase.ruta.ListarRutasUseCase;
import com.xpedia.backend.domain.useCase.ruta.ObtenerRutaUseCase;
import com.xpedia.backend.infrastructure.presentation.dto.ruta.*;
import com.xpedia.backend.infrastructure.presentation.mapper.ruta.RutaPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rutas")
@Tag(name = "Rutas", description = "Catálogo público de rutas de Xpedia")
public class RutaController {
    private final ListarRutasUseCase listarRutasUseCase;
    private final ObtenerRutaUseCase obtenerRutaUseCase;
    private final RutaPresentationMapper mapper;

    @GetMapping
    @Operation(summary = "Listar rutas globales publicadas",
            description = "Filtros opcionales tipo y objetivo. Página desde 0; tamaño entre 1 y 100. Orden por título e ID.")
    public ResponseEntity<RutaPageResponse> listar(@Valid @ModelAttribute @ParameterObject ListarRutasQuery query) {
        return ResponseEntity.ok(mapper.toPageResponse(listarRutasUseCase.execute(mapper.toListarRequest(query))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una ruta global publicada",
            description = "Devuelve 404 si la ruta no existe o no pertenece al catálogo público.")
    public ResponseEntity<RutaResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(obtenerRutaUseCase.execute(mapper.toObtenerRequest(id))));
    }
}
