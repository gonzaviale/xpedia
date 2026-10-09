package com.xpedia.backend.domain.useCase.auth;

import com.xpedia.backend.domain.dto.auth.IniciarSesionRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.mapper.auth.IniciarSesionMapper;
import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.domain.service.auth.AuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IniciarSesionUseCase {

    private final AuthService authService;

    private final IniciarSesionMapper iniciarSesionMapper;

    public UsuarioItem execute(IniciarSesionRequest request) {
        Usuario usuario = authService.autenticar(request.email(), request.contrasenia());
        return iniciarSesionMapper.toResponse(usuario);
    }
}
