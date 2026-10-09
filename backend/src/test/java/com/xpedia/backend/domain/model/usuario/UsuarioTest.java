package com.xpedia.backend.domain.model.usuario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    @DisplayName("Normaliza email con espacios y mayúsculas")
    void normalizarEmailShouldTrimAndLowercase() {
        String email = Usuario.normalizarEmail(" Persona@Example.COM ");

        thenNormalizarEmailShouldTrimAndLowercase(email);
    }

    @Test
    @DisplayName("Reconoce al usuario activo")
    void estaActivoShouldReturnTrueWhenActive() {
        Usuario usuario = Usuario.builder().estado("ACTIVO").build();

        thenEstaActivoShouldReturnTrueWhenActive(usuario);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"SUSPENDIDO", ""})
    @DisplayName("No reconoce estados nulos o suspendidos como activos")
    void estaActivoShouldReturnFalseWhenNotActive(String estado) {
        Usuario usuario = Usuario.builder().estado(estado).build();

        assertThat(usuario.estaActivo()).isFalse();
    }

    // --- assert ---
    private void thenNormalizarEmailShouldTrimAndLowercase(String email) {
        assertThat(email).isEqualTo("persona@example.com");
    }

    private void thenEstaActivoShouldReturnTrueWhenActive(Usuario usuario) {
        assertThat(usuario.estaActivo()).isTrue();
    }
}
