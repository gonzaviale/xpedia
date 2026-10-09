package com.xpedia.backend.infrastructure.repository.jpaRepository.interfaces;

import com.xpedia.backend.infrastructure.repository.entity.UsuarioEntity;
import com.xpedia.backend.support.PostgresRepositoryTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static com.xpedia.backend.support.UsuarioTestData.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql("/db/usuarios-test.sql")
class IUsuarioJpaRepositoryTest extends PostgresRepositoryTestSupport {

    @Autowired
    private IUsuarioJpaRepository usuarioJpaRepository;

    @Test
    @DisplayName("Encuentra emails antiguos con espacios y mayúsculas")
    void findByEmailNormalizadoShouldFindLegacyEmail() {
        Optional<UsuarioEntity> result = usuarioJpaRepository.findByEmailNormalizado("anterior@example.com");

        thenFindByEmailNormalizadoShouldFindLegacyEmail(result);
    }

    @Test
    @DisplayName("Devuelve vacío si no existe el email")
    void findByEmailNormalizadoShouldReturnEmptyWhenMissing() {
        thenFindByEmailNormalizadoShouldReturnEmptyWhenMissing();
    }

    @Test
    @DisplayName("El índice impide duplicados normalizados aun saltando la validación de aplicación")
    void saveShouldRejectNormalizedDuplicateEmail() {
        UsuarioEntity entity = entity();
        entity.setId(null);
        entity.setEmail(" PERSONA@EXAMPLE.COM ");

        thenSaveShouldRejectNormalizedDuplicateEmail(entity);
    }

    @Test
    @DisplayName("Persiste los campos de una cuenta nueva sin perder el hash")
    void saveShouldPersistEveryUsuarioField() {
        UsuarioEntity entity = entity();
        entity.setId(null);
        entity.setEmail("nueva@example.com");
        UsuarioEntity saved = usuarioJpaRepository.saveAndFlush(entity);
        UsuarioEntity result = usuarioJpaRepository.findById(saved.getId()).orElseThrow();

        thenSaveShouldPersistEveryUsuarioField(entity, result);
    }

    // --- assert ---
    private void thenFindByEmailNormalizadoShouldFindLegacyEmail(Optional<UsuarioEntity> result) {
        assertThat(result.orElseThrow().getNombre()).isEqualTo("Anterior");
    }

    private void thenFindByEmailNormalizadoShouldReturnEmptyWhenMissing() {
        assertThat(usuarioJpaRepository.findByEmailNormalizado("ausente@example.com")).isEmpty();
    }

    private void thenSaveShouldRejectNormalizedDuplicateEmail(UsuarioEntity entity) {
        assertThatThrownBy(() -> usuarioJpaRepository.saveAndFlush(entity))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private void thenSaveShouldPersistEveryUsuarioField(UsuarioEntity entity, UsuarioEntity result) {
        assertThat(result.getId()).isNotNull();
        assertThat(result.getNombre()).isEqualTo(entity.getNombre());
        assertThat(result.getEmail()).isEqualTo("nueva@example.com");
        assertThat(result.getHashContrasenia()).isEqualTo(entity.getHashContrasenia());
        assertThat(result.getTipo()).isEqualTo("PERSONA");
        assertThat(result.getEstado()).isEqualTo("ACTIVO");
        assertThat(result.getCreadoEn()).isEqualTo(entity.getCreadoEn());
        assertThat(result.getActualizadoEn()).isEqualTo(entity.getActualizadoEn());
    }
}
