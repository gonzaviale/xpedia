package com.xpedia.backend.infrastructure.adapter;

import com.xpedia.backend.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static org.assertj.core.api.Assertions.assertThat;

class ContraseniaAdapterTest {

    private final ContraseniaAdapter contraseniaAdapter =
            new ContraseniaAdapter(new SecurityConfig().passwordEncoder());

    @Test
    @DisplayName("Codifica con salt y permite verificar la contraseña")
    void codificarShouldCreateVerifiableSaltedHash() {
        String hash = contraseniaAdapter.codificar(CONTRASENIA);

        thenCodificarShouldCreateVerifiableSaltedHash(hash);
    }

    @Test
    @DisplayName("No acepta una contraseña diferente")
    void coincideShouldRejectWrongPassword() {
        String hash = contraseniaAdapter.codificar(CONTRASENIA);

        thenCoincideShouldRejectWrongPassword(hash);
    }

    @Test
    @DisplayName("No acepta un hash nulo y realiza la comparación ficticia")
    void coincideShouldRejectNullHash() {
        thenCoincideShouldRejectNullHash();
    }

    @Test
    @DisplayName("No acepta un hash desconocido o mal formado")
    void coincideShouldRejectMalformedHash() {
        thenCoincideShouldRejectMalformedHash();
    }

    // --- assert ---
    private void thenCodificarShouldCreateVerifiableSaltedHash(String hash) {
        assertThat(hash).startsWith("{bcrypt}").doesNotContain(CONTRASENIA);
        assertThat(contraseniaAdapter.coincide(CONTRASENIA, hash)).isTrue();
        assertThat(contraseniaAdapter.codificar(CONTRASENIA)).isNotEqualTo(hash);
    }

    private void thenCoincideShouldRejectWrongPassword(String hash) {
        assertThat(contraseniaAdapter.coincide("Contraseña diferente", hash)).isFalse();
    }

    private void thenCoincideShouldRejectNullHash() {
        assertThat(contraseniaAdapter.coincide(CONTRASENIA, null)).isFalse();
    }

    private void thenCoincideShouldRejectMalformedHash() {
        assertThat(contraseniaAdapter.coincide(CONTRASENIA, "{otro}invalido")).isFalse();
    }
}
