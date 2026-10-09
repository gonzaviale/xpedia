package com.xpedia.backend.domain.mapper.auth;

import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.model.usuario.Usuario;

public class ObtenerUsuarioActualMapper {

    public UsuarioItem toResponse(Usuario usuario) {
        return new UsuarioItem(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getTipo());
    }
}
