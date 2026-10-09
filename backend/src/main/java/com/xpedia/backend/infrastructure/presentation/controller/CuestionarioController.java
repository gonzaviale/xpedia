package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.useCase.cuestionario.ListarCuestionariosUseCase;
import com.xpedia.backend.infrastructure.presentation.dto.cuestionario.CuestionarioResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.cuestionario.CuestionarioPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rutas/{rutaId}/nodos/{nodoId}/cuestionarios")
@Tag(name = "Cuestionarios", description = "Preguntas de opción múltiple aprobadas de cada nodo")
public class CuestionarioController {

    private final ListarCuestionariosUseCase listarCuestionariosUseCase;
    private final CuestionarioPresentationMapper cuestionarioPresentationMapper;

    @GetMapping
    @Operation(summary = "Listar cuestionarios de un nodo",
            description = "CUESTIONARIO aprobados con al menos una pregunta, sin la opción correcta ni la "
                    + "explicación: esos datos solo se devuelven al registrar un intento. "
                    + "Nodo sin cuestionarios: 200 con []. Ruta o nodo oculto o inexistente: 404.")
    public ResponseEntity<List<CuestionarioResponse>> listar(
            @PathVariable UUID rutaId,
            @PathVariable UUID nodoId) {
        return ResponseEntity.ok(
                cuestionarioPresentationMapper.toResponse(
                        listarCuestionariosUseCase.execute(
                                cuestionarioPresentationMapper.toListarRequest(rutaId, nodoId))));
    }
}
