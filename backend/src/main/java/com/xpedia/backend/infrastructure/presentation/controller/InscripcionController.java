package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.inscripcion.InscripcionItem;
import com.xpedia.backend.domain.useCase.inscripcion.CrearInscripcionUseCase;
import com.xpedia.backend.domain.useCase.inscripcion.ObtenerInscripcionActualUseCase;
import com.xpedia.backend.infrastructure.presentation.dto.inscripcion.CrearInscripcionWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.inscripcion.InscripcionResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.inscripcion.InscripcionPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final CrearInscripcionUseCase crearInscripcionUseCase;

    private final ObtenerInscripcionActualUseCase obtenerInscripcionActualUseCase;

    private final InscripcionPresentationMapper inscripcionPresentationMapper;

    @PostMapping
    @SecurityRequirement(name = "sesion")
    @SecurityRequirement(name = "csrf")
    @Operation(summary = "Elegir ruta y ritmo, creando el progreso inicial")
    public ResponseEntity<InscripcionResponse> crear(
            @Valid @RequestBody CrearInscripcionWebRequest request, Authentication authentication) {
        InscripcionItem item = crearInscripcionUseCase.execute(
                inscripcionPresentationMapper.toRequest(UUID.fromString(authentication.getName()), request));
        return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noStore())
                .body(inscripcionPresentationMapper.toResponse(item));
    }

    @GetMapping("/actual")
    @SecurityRequirement(name = "sesion")
    @Operation(summary = "Consultar la inscripción activa más reciente del usuario")
    public ResponseEntity<InscripcionResponse> actual(Authentication authentication) {
        InscripcionItem item = obtenerInscripcionActualUseCase.execute(
                inscripcionPresentationMapper.toActualRequest(UUID.fromString(authentication.getName())));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(inscripcionPresentationMapper.toResponse(item));
    }
}
