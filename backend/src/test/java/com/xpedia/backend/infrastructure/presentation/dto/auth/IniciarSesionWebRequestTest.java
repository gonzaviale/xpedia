package com.xpedia.backend.infrastructure.presentation.dto.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IniciarSesionWebRequestTest {

    @Test
    @DisplayName("Recorta el email conservando la contraseña intacta")
    void constructorShouldTrimEmailAndKeepPassword() {
        IniciarSesionWebRequest request = new IniciarSesionWebRequest(" PERSONA@example.com ",
                " contraseña con espacios ");

        thenConstructorShouldTrimEmailAndKeepPassword(request);
    }

    @Test
    @DisplayName("Conserva el email nulo para que la validación lo rechace")
    void constructorShouldPreserveNullEmail() {
        IniciarSesionWebRequest request = new IniciarSesionWebRequest(null, null);

        thenConstructorShouldPreserveNullEmail(request);
    }

    @Test
    @DisplayName("Admite exactamente 72 bytes ASCII")
    void isContraseniaValidaShouldAccept72Bytes() {
        IniciarSesionWebRequest request = new IniciarSesionWebRequest(" PERSONA@example.com ", "a".repeat(72));

        thenIsContraseniaValidaShouldAccept72Bytes(request);
    }

    @Test
    @DisplayName("Rechaza más de 72 bytes aunque haya menos caracteres Unicode")
    void isContraseniaValidaShouldRejectLongUnicodePassword() {
        IniciarSesionWebRequest request = new IniciarSesionWebRequest(" PERSONA@example.com ", "ñ".repeat(37));

        thenIsContraseniaValidaShouldRejectLongUnicodePassword(request);
    }

    // --- assert ---
    private void thenConstructorShouldTrimEmailAndKeepPassword(IniciarSesionWebRequest request) {
        assertThat(request.email()).isEqualTo("PERSONA@example.com");
        assertThat(request.contrasenia()).isEqualTo(" contraseña con espacios ");
    }

    private void thenConstructorShouldPreserveNullEmail(IniciarSesionWebRequest request) {
        assertThat(request.email()).isNull();
        assertThat(request.isContraseniaValida()).isTrue();
    }

    private void thenIsContraseniaValidaShouldAccept72Bytes(IniciarSesionWebRequest request) {
        assertThat(request.isContraseniaValida()).isTrue();
    }

    private void thenIsContraseniaValidaShouldRejectLongUnicodePassword(IniciarSesionWebRequest request) {
        assertThat(request.isContraseniaValida()).isFalse();
    }
}
