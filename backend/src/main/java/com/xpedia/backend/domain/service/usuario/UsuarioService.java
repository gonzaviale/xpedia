package com.xpedia.backend.domain.service.usuario;

import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.domain.port.ContraseniaPort;
import com.xpedia.backend.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final ContraseniaPort contraseniaPort;

    @Transactional
    public Usuario registrar(String nombre, String email, String contrasenia, OffsetDateTime fecha) {
        String normalizado = Usuario.normalizarEmail(email);
        validarEmailLibre(normalizado);
        Usuario usuario = Usuario.builder()
                .nombre(nombre.trim())
                .email(normalizado)
                .hashContrasenia(contraseniaPort.codificar(contrasenia))
                .tipo("PERSONA")
                .estado("ACTIVO")
                .creadoEn(fecha)
                .actualizadoEn(fecha)
                .build();
        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerActivo(UUID id) {
        return usuarioRepository.findById(id)
                .filter(Usuario::estaActivo)
                .orElseThrow(CredencialesInvalidasException::new);
    }

    private void validarEmailLibre(String email) {
        if (usuarioRepository.findByEmailNormalizado(email).isPresent()) {
            throw new DuplicateResourceException("usuario", "email", email);
        }
    }
}
