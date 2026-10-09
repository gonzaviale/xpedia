package com.xpedia.backend.domain.useCase.usuario;

import com.xpedia.backend.domain.dto.usuario.RegistrarUsuarioRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.mapper.usuario.RegistrarUsuarioMapper;
import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioService usuarioService;

    private final RegistrarUsuarioMapper registrarUsuarioMapper;

    private final Clock clock;

    public UsuarioItem execute(RegistrarUsuarioRequest request) {
        Usuario usuario = usuarioService.registrar(request.nombre(), request.email(), request.contrasenia(),
                OffsetDateTime.now(clock));
        return registrarUsuarioMapper.toResponse(usuario);
    }
}
