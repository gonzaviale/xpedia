package com.xpedia.backend.domain.service.auth;

import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.domain.port.ContraseniaPort;
import com.xpedia.backend.domain.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.HASH;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContraseniaPort contraseniaPort;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Autentica email normalizado y contraseña correcta")
    void autenticarShouldReturnActiveUsuarioWhenPasswordMatches() {
        givenUsuario("ACTIVO", true);

        Usuario result = autenticar();

        thenAutenticarShouldReturnActiveUsuarioWhenPasswordMatches(result);
    }

    @Test
    @DisplayName("Rechaza contraseña incorrecta sin exponer el motivo")
    void autenticarShouldRejectWrongPassword() {
        givenUsuario("ACTIVO", false);

        thenAutenticarShouldRejectWrongPassword();
    }

    @Test
    @DisplayName("Rechaza suspendidos aun con contraseña correcta")
    void autenticarShouldRejectSuspendedUsuario() {
        givenUsuario("SUSPENDIDO", true);

        thenAutenticarShouldRejectSuspendedUsuario();
    }

    @Test
    @DisplayName("Compara contra hash ficticio cuando no existe el email")
    void autenticarShouldCompareDummyHashWhenUsuarioIsMissing() {
        thenAutenticarShouldCompareDummyHashWhenUsuarioIsMissing();
    }

    @Test
    @DisplayName("Rechaza una cuenta sin hash de contraseña")
    void autenticarShouldRejectUsuarioWithoutHash() {
        Usuario usuario = usuario();
        usuario.setHashContrasenia(null);
        when(usuarioRepository.findByEmailNormalizado(EMAIL)).thenReturn(Optional.of(usuario));

        thenAutenticarShouldRejectUsuarioWithoutHash();
    }

    // --- arrange ---
    private void givenUsuario(String estado, boolean coincide) {
        Usuario usuario = usuario();
        usuario.setEstado(estado);
        when(usuarioRepository.findByEmailNormalizado(EMAIL)).thenReturn(Optional.of(usuario));
        when(contraseniaPort.coincide(CONTRASENIA, HASH)).thenReturn(coincide);
    }

    // --- act ---
    private Usuario autenticar() {
        return authService.autenticar(" PERSONA@EXAMPLE.COM ", CONTRASENIA);
    }

    // --- assert ---
    private void thenAutenticarShouldReturnActiveUsuarioWhenPasswordMatches(Usuario result) {
        assertThat(result.getId()).isEqualTo(USUARIO_ID);
    }

    private void thenAutenticarShouldRejectWrongPassword() {
        assertThatThrownBy(this::autenticar).isInstanceOf(CredencialesInvalidasException.class);
    }

    private void thenAutenticarShouldRejectSuspendedUsuario() {
        assertThatThrownBy(this::autenticar).isInstanceOf(CredencialesInvalidasException.class);
    }

    private void thenAutenticarShouldCompareDummyHashWhenUsuarioIsMissing() {
        assertThatThrownBy(this::autenticar).isInstanceOf(CredencialesInvalidasException.class);
        verify(contraseniaPort).coincide(CONTRASENIA, null);
    }

    private void thenAutenticarShouldRejectUsuarioWithoutHash() {
        assertThatThrownBy(this::autenticar).isInstanceOf(CredencialesInvalidasException.class);
        verify(contraseniaPort).coincide(CONTRASENIA, null);
    }
}
