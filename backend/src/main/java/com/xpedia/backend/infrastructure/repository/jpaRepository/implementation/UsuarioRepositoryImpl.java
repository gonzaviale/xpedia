package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.domain.repository.usuario.UsuarioRepository;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IUsuarioJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.UsuarioRepositoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final IUsuarioJpaRepository usuarioJpaRepository;

    private final UsuarioRepositoryMapper usuarioRepositoryMapper;

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepositoryMapper.toDomain(
                usuarioJpaRepository.saveAndFlush(usuarioRepositoryMapper.toEntity(usuario)));
    }

    @Override
    public Optional<Usuario> findByEmailNormalizado(String email) {
        return usuarioJpaRepository.findByEmailNormalizado(email).map(usuarioRepositoryMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return usuarioJpaRepository.findById(id).map(usuarioRepositoryMapper::toDomain);
    }
}
