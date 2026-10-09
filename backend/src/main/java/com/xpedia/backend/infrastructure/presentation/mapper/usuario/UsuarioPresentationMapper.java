package com.xpedia.backend.infrastructure.presentation.mapper.usuario;

import com.xpedia.backend.domain.dto.usuario.RegistrarUsuarioRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.RegistrarUsuarioWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.UsuarioResponse;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPresentationMapper {

    public RegistrarUsuarioRequest toRequest(RegistrarUsuarioWebRequest request) {
        return new RegistrarUsuarioRequest(request.nombre(), request.email(), request.contrasenia());
    }

    public UsuarioResponse toResponse(UsuarioItem item) {
        return new UsuarioResponse(item.id(), item.nombre(), item.email(), item.tipo());
    }
}
