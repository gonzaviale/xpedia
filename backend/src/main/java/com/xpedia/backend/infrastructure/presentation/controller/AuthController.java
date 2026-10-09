package com.xpedia.backend.infrastructure.presentation.controller;

import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.useCase.auth.IniciarSesionUseCase;
import com.xpedia.backend.domain.useCase.auth.ObtenerUsuarioActualUseCase;
import com.xpedia.backend.infrastructure.presentation.dto.auth.CsrfResponse;
import com.xpedia.backend.infrastructure.presentation.dto.auth.IniciarSesionWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.UsuarioResponse;
import com.xpedia.backend.infrastructure.presentation.mapper.auth.AuthPresentationMapper;
import com.xpedia.backend.infrastructure.security.SesionAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Acceso")
@RequiredArgsConstructor
public class AuthController {

    private final IniciarSesionUseCase iniciarSesionUseCase;

    private final ObtenerUsuarioActualUseCase obtenerUsuarioActualUseCase;

    private final AuthPresentationMapper authPresentationMapper;

    private final SesionAdapter sesionAdapter;

    @GetMapping("/csrf")
    @Operation(summary = "Obtener el token CSRF para las escrituras de la sesión")
    public ResponseEntity<CsrfResponse> csrf(@RequestAttribute("_csrf") CsrfToken csrfToken) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(new CsrfResponse(csrfToken.getToken(), csrfToken.getHeaderName()));
    }

    @PostMapping("/login")
    @SecurityRequirement(name = "csrf")
    @Operation(summary = "Iniciar sesión con email y contraseña")
    public ResponseEntity<UsuarioResponse> iniciar(@Valid @RequestBody IniciarSesionWebRequest request,
                                                  HttpServletRequest servletRequest,
                                                  HttpServletResponse servletResponse) {
        UsuarioItem usuario = iniciarSesionUseCase.execute(authPresentationMapper.toRequest(request));
        sesionAdapter.iniciar(usuario.id(), usuario.tipo(), servletRequest, servletResponse);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(authPresentationMapper.toResponse(usuario));
    }

    @GetMapping("/actual")
    @SecurityRequirement(name = "sesion")
    @Operation(summary = "Consultar el usuario de la sesión actual")
    public ResponseEntity<UsuarioResponse> actual(Authentication authentication) {
        UsuarioItem usuario = obtenerUsuarioActualUseCase.execute(
                authPresentationMapper.toActualRequest(UUID.fromString(authentication.getName())));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(authPresentationMapper.toResponse(usuario));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "csrf")
    @Operation(summary = "Cerrar e invalidar la sesión actual")
    public ResponseEntity<Void> cerrar(HttpServletRequest request, HttpServletResponse response) {
        sesionAdapter.cerrar(request, response);
        return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build();
    }
}
