package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.useCase.usuario.RegistrarUsuarioUseCase;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.RegistrarUsuarioWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.UsuarioResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.usuario.UsuarioPresentationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    private final UsuarioPresentationMapper usuarioPresentationMapper;

    @PostMapping("/registro")
    @SecurityRequirement(name = "csrf")
    @Operation(summary = "Registrar una cuenta individual con email y contraseña")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistrarUsuarioWebRequest request) {
        UsuarioItem response = registrarUsuarioUseCase.execute(usuarioPresentationMapper.toRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).cacheControl(CacheControl.noStore())
                .body(usuarioPresentationMapper.toResponse(response));
    }
}
