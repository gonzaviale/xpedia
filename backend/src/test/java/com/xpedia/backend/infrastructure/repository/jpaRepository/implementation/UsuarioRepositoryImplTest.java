package com.xpedia.backend.infrastructure.repository.jpaRepository.implementation;

import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces.IUsuarioJpaRepository;
import com.xpedia.backend.infrastructure.repository.mapper.UsuarioRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.entity;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioRepositoryImplTest {

    private IUsuarioJpaRepository usuarioJpaRepository;

    private UsuarioRepositoryImpl usuarioRepository;

    @BeforeEach
    void setUp() {
        usuarioJpaRepository = mock(IUsuarioJpaRepository.class);
        usuarioRepository = new UsuarioRepositoryImpl(usuarioJpaRepository, new UsuarioRepositoryMapper());
    }

    @Test
    @DisplayName("Guarda y fuerza el chequeo de restricciones antes de devolver el dominio")
    void saveShouldFlushAndReturnUsuario() {
        givenSaveShouldFlushAndReturnUsuario();

        Usuario result = usuarioRepository.save(usuario());

        thenSaveShouldFlushAndReturnUsuario(result);
    }

    @Test
    @DisplayName("Obtiene el usuario por email normalizado")
    void findByEmailNormalizadoShouldMapFoundUsuario() {
        givenFindByEmailNormalizadoShouldMapFoundUsuario();
        Optional<Usuario> result = usuarioRepository.findByEmailNormalizado(EMAIL);

        thenFindByEmailNormalizadoShouldMapFoundUsuario(result);
    }

    @Test
    @DisplayName("Conserva ausencia de usuario al consultar email")
    void findByEmailNormalizadoShouldReturnEmptyWhenMissing() {
        thenFindByEmailNormalizadoShouldReturnEmptyWhenMissing();
    }

    @Test
    @DisplayName("Obtiene el usuario por identificador")
    void findByIdShouldMapFoundUsuario() {
        when(usuarioJpaRepository.findById(USUARIO_ID)).thenReturn(Optional.of(entity()));

        thenFindByIdShouldMapFoundUsuario();
    }

    @Test
    @DisplayName("Conserva ausencia de usuario al consultar identificador")
    void findByIdShouldReturnEmptyWhenMissing() {
        thenFindByIdShouldReturnEmptyWhenMissing();
    }

    // --- arrange ---
    private void givenSaveShouldFlushAndReturnUsuario() {
        when(usuarioJpaRepository.saveAndFlush(any())).thenReturn(entity());
    }

    private void givenFindByEmailNormalizadoShouldMapFoundUsuario() {
        when(usuarioJpaRepository.findByEmailNormalizado(EMAIL)).thenReturn(Optional.of(entity()));
    }

    // --- assert ---
    private void thenSaveShouldFlushAndReturnUsuario(Usuario result) {
        assertThat(result.getId()).isEqualTo(USUARIO_ID);
        verify(usuarioJpaRepository).saveAndFlush(any());
    }

    private void thenFindByEmailNormalizadoShouldMapFoundUsuario(Optional<Usuario> result) {
        assertThat(result.orElseThrow().getEmail()).isEqualTo(EMAIL);
    }

    private void thenFindByEmailNormalizadoShouldReturnEmptyWhenMissing() {
        assertThat(usuarioRepository.findByEmailNormalizado(EMAIL)).isEmpty();
    }

    private void thenFindByIdShouldMapFoundUsuario() {
        assertThat(usuarioRepository.findById(USUARIO_ID).orElseThrow().getId()).isEqualTo(USUARIO_ID);
    }

    private void thenFindByIdShouldReturnEmptyWhenMissing() {
        assertThat(usuarioRepository.findById(USUARIO_ID)).isEmpty();
    }
}
