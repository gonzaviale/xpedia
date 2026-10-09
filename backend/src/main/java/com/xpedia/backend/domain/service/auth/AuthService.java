package com.xpedia.backend.domain.service.auth;

import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.domain.port.ContraseniaPort;
import com.xpedia.backend.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    private final ContraseniaPort contraseniaPort;

    public Usuario autenticar(String email, String contrasenia) {
        Usuario usuario = usuarioRepository.findByEmailNormalizado(Usuario.normalizarEmail(email))
                .orElseGet(Usuario::new);
        boolean coincide = contraseniaPort.coincide(contrasenia, usuario.getHashContrasenia());
        if (!coincide || !usuario.estaActivo()) {
            throw new CredencialesInvalidasException();
        }
        return usuario;
    }
}
