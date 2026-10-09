package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.useCase.microleccion.ListarMicroleccionesUseCase;
import com.xpedia.backend.infrastructure.presentation.dto.microleccion.MicroleccionResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.microleccion.MicroleccionPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rutas/{rutaId}/nodos/{nodoId}/microlecciones")
@Tag(name = "Microlecciones", description = "Material aprobado con fuentes del catálogo público")
public class MicroleccionController {

    private final ListarMicroleccionesUseCase listarMicroleccionesUseCase;
    private final MicroleccionPresentationMapper microleccionPresentationMapper;

    @GetMapping
    @Operation(summary = "Consultar microlecciones de un nodo",
            description = "Ruta global publicada y nodo de esa ruta. Solo material aprobado con fuentes globales; "
                    + "200 con [] si no hay material visible, 404 si ruta/nodo no es visible "
                    + "y 400 si el UUID es inválido.")
    public ResponseEntity<List<MicroleccionResponse>> listar(
            @PathVariable UUID rutaId,
            @PathVariable UUID nodoId) {
        return ResponseEntity.ok(
                microleccionPresentationMapper.toResponse(
                        listarMicroleccionesUseCase.execute(
                                microleccionPresentationMapper.toRequest(rutaId, nodoId))));
    }
}
