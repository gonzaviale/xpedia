package com.xpedia.backend.infrastructure.presentation.dto.usuario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarUsuarioWebRequestTest {

    @Test
    @DisplayName("Recorta el email conservando la contraseña intacta")
    void constructorShouldTrimEmailAndKeepPassword() {
        RegistrarUsuarioWebRequest request = new RegistrarUsuarioWebRequest("Persona", " PERSONA@example.com ",
                " contraseña con espacios ");

        thenConstructorShouldTrimEmailAndKeepPassword(request);
    }

    @Test
    @DisplayName("Conserva el email nulo para que la validación lo rechace")
    void constructorShouldPreserveNullEmail() {
        RegistrarUsuarioWebRequest request = new RegistrarUsuarioWebRequest("Persona", null, null);

        thenConstructorShouldPreserveNullEmail(request);
    }

    @Test
    @DisplayName("Admite exactamente 72 bytes ASCII")
    void isContraseniaValidaShouldAccept72Bytes() {
        RegistrarUsuarioWebRequest request = new RegistrarUsuarioWebRequest("Persona", " PERSONA@example.com ",
                "a".repeat(72));

        thenIsContraseniaValidaShouldAccept72Bytes(request);
    }

    @Test
    @DisplayName("Rechaza más de 72 bytes aunque haya menos caracteres Unicode")
    void isContraseniaValidaShouldRejectLongUnicodePassword() {
        RegistrarUsuarioWebRequest request = new RegistrarUsuarioWebRequest("Persona", " PERSONA@example.com ",
                "ñ".repeat(37));

        thenIsContraseniaValidaShouldRejectLongUnicodePassword(request);
    }

    // --- assert ---
    private void thenConstructorShouldTrimEmailAndKeepPassword(RegistrarUsuarioWebRequest request) {
        assertThat(request.email()).isEqualTo("PERSONA@example.com");
        assertThat(request.contrasenia()).isEqualTo(" contraseña con espacios ");
    }

    private void thenConstructorShouldPreserveNullEmail(RegistrarUsuarioWebRequest request) {
        assertThat(request.email()).isNull();
        assertThat(request.isContraseniaValida()).isTrue();
    }

    private void thenIsContraseniaValidaShouldAccept72Bytes(RegistrarUsuarioWebRequest request) {
        assertThat(request.isContraseniaValida()).isTrue();
    }

    private void thenIsContraseniaValidaShouldRejectLongUnicodePassword(RegistrarUsuarioWebRequest request) {
        assertThat(request.isContraseniaValida()).isFalse();
    }
}
