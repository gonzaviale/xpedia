package com.xpedia.backend.domain.repository.usuario;

import com.xpedia.backend.domain.model.usuario.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findByEmailNormalizado(String email);

    Optional<Usuario> findById(UUID id);
}
