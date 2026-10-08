package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.useCase.puesto.*;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoPageResponse;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoRequest;
import com.xpedia.backend.infrastructure.presentation.dto.puesto.PuestoResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.puesto.PuestoPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/puestos")
public class PuestoController {

    private final CrearPuestoUseCase crearPuestoUseCase;
    private final ActualizarPuestoUseCase actualizarPuestoUseCase;
    private final EliminarPuestoUseCase eliminarPuestoUseCase;
    private final ObtenerPuestoUseCase obtenerPuestoUseCase;
    private final ListarPuestosUseCase listarPuestosUseCase;
    private final PuestoPresentationMapper puestoPresentationMapper;

    @PostMapping
    public ResponseEntity<PuestoResponse> crear(@Valid @RequestBody PuestoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                puestoPresentationMapper.toResponse(
                        crearPuestoUseCase.execute(puestoPresentationMapper.toCrearRequest(request))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PuestoResponse> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(
                puestoPresentationMapper.toResponse(
                        obtenerPuestoUseCase.execute(puestoPresentationMapper.toObtenerRequest(id))));
    }

    @GetMapping
    public ResponseEntity<PuestoPageResponse> listar(
            @RequestParam(required = false) UUID organizacionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                puestoPresentationMapper.toPageResponse(
                        listarPuestosUseCase.execute(
                                puestoPresentationMapper.toListarRequest(organizacionId, page, size))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PuestoResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody PuestoRequest request) {
        return ResponseEntity.ok(
                puestoPresentationMapper.toResponse(
                        actualizarPuestoUseCase.execute(puestoPresentationMapper.toActualizarRequest(id, request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        eliminarPuestoUseCase.execute(puestoPresentationMapper.toEliminarRequest(id));
        return ResponseEntity.noContent().build();
    }
}
