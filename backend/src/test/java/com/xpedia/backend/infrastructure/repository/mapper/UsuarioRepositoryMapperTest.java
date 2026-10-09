package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.infrastructure.repository.entity.UsuarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.FECHA;
import static com.xpedia.backend.support.UsuarioTestData.HASH;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.entity;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;

class UsuarioRepositoryMapperTest {

    private final UsuarioRepositoryMapper usuarioRepositoryMapper = new UsuarioRepositoryMapper();

    @Test
    @DisplayName("Mapea cada campo de la entidad al dominio")
    void toDomainShouldMapEveryField() {
        Usuario result = usuarioRepositoryMapper.toDomain(entity());

        thenToDomainShouldMapEveryField(result);
    }

    @Test
    @DisplayName("Mapea cada campo del dominio a la entidad")
    void toEntityShouldMapEveryField() {
        UsuarioEntity result = usuarioRepositoryMapper.toEntity(usuario());

        thenToEntityShouldMapEveryField(result);
    }

    @Test
    @DisplayName("Conserva todos los nulos de una entidad vacía")
    void toDomainShouldPreserveNulls() {
        Usuario result = usuarioRepositoryMapper.toDomain(new UsuarioEntity());

        thenToDomainShouldPreserveNulls(result);
    }

    @Test
    @DisplayName("Conserva todos los nulos de un dominio vacío")
    void toEntityShouldPreserveNulls() {
        UsuarioEntity result = usuarioRepositoryMapper.toEntity(new Usuario());

        thenToEntityShouldPreserveNulls(result);
    }

    // --- assert ---
    private void thenToDomainShouldMapEveryField(Usuario result) {
        assertThat(result.getId()).isEqualTo(USUARIO_ID);
        assertThat(result.getNombre()).isEqualTo(NOMBRE);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getHashContrasenia()).isEqualTo(HASH);
        assertThat(result.getTipo()).isEqualTo("PERSONA");
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        assertThat(result.getCreadoEn()).isEqualTo(FECHA);
        assertThat(result.getActualizadoEn()).isEqualTo(FECHA);
    }

    private void thenToEntityShouldMapEveryField(UsuarioEntity result) {
        assertThat(result.getId()).isEqualTo(USUARIO_ID);
        assertThat(result.getNombre()).isEqualTo(NOMBRE);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getHashContrasenia()).isEqualTo(HASH);
        assertThat(result.getTipo()).isEqualTo("PERSONA");
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        assertThat(result.getCreadoEn()).isEqualTo(FECHA);
        assertThat(result.getActualizadoEn()).isEqualTo(FECHA);
    }

    private void thenToDomainShouldPreserveNulls(Usuario result) {
        assertThat(result.getId()).isNull();
        assertThat(result.getNombre()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getHashContrasenia()).isNull();
        assertThat(result.getTipo()).isNull();
        assertThat(result.getEstado()).isNull();
        assertThat(result.getCreadoEn()).isNull();
        assertThat(result.getActualizadoEn()).isNull();
    }

    private void thenToEntityShouldPreserveNulls(UsuarioEntity result) {
        assertThat(result.getId()).isNull();
        assertThat(result.getNombre()).isNull();
        assertThat(result.getEmail()).isNull();
        assertThat(result.getHashContrasenia()).isNull();
        assertThat(result.getTipo()).isNull();
        assertThat(result.getEstado()).isNull();
        assertThat(result.getCreadoEn()).isNull();
        assertThat(result.getActualizadoEn()).isNull();
    }
}
