package com.xpedia.backend.domain.useCase.auth;

import com.xpedia.backend.domain.dto.auth.ObtenerUsuarioActualRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.mapper.auth.ObtenerUsuarioActualMapper;
import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObtenerUsuarioActualUseCase {

    private final UsuarioService usuarioService;

    private final ObtenerUsuarioActualMapper obtenerUsuarioActualMapper;

    public UsuarioItem execute(ObtenerUsuarioActualRequest request) {
        Usuario usuario = usuarioService.obtenerActivo(request.usuarioId());
        return obtenerUsuarioActualMapper.toResponse(usuario);
    }
}
