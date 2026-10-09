package com.xpedia.backend.infrastructure.presentation.mapper.auth;

import com.xpedia.backend.domain.dto.auth.IniciarSesionRequest;
import com.xpedia.backend.domain.dto.auth.ObtenerUsuarioActualRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.infrastructure.presentation.dto.auth.IniciarSesionWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.UsuarioResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthPresentationMapper {

    public IniciarSesionRequest toRequest(IniciarSesionWebRequest request) {
        return new IniciarSesionRequest(request.email(), request.contrasenia());
    }

    public ObtenerUsuarioActualRequest toActualRequest(UUID id) {
        return new ObtenerUsuarioActualRequest(id);
    }

    public UsuarioResponse toResponse(UsuarioItem item) {
        return new UsuarioResponse(item.id(), item.nombre(), item.email(), item.tipo());
    }
}
