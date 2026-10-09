package com.xpedia.backend.domain.service.usuario;

import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.exception.DuplicateResourceException;
import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.domain.port.ContraseniaPort;
import com.xpedia.backend.domain.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.FECHA;
import static com.xpedia.backend.support.UsuarioTestData.HASH;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ContraseniaPort contraseniaPort;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Registra persona activa con nombre y email normalizados y contraseña codificada")
    void registrarShouldSaveNormalizedPersona() {
        givenEmailIsFree();

        Usuario result = registrar();

        thenRegistrarShouldSaveNormalizedPersona(result);
    }

    @Test
    @DisplayName("No codifica ni guarda cuando el email está ocupado")
    void registrarShouldRejectDuplicateBeforeEncoding() {
        when(usuarioRepository.findByEmailNormalizado(EMAIL)).thenReturn(Optional.of(usuario()));

        thenRegistrarShouldRejectDuplicateBeforeEncoding();
    }

    @Test
    @DisplayName("Obtiene al usuario activo por su identificador")
    void obtenerActivoShouldReturnActiveUsuario() {
        givenObtenerActivoShouldReturnActiveUsuario();

        Usuario result = usuarioService.obtenerActivo(USUARIO_ID);

        thenObtenerActivoShouldReturnActiveUsuario(result);
    }

    @Test
    @DisplayName("Rechaza el usuario inexistente con error genérico de acceso")
    void obtenerActivoShouldRejectMissingUsuario() {
        thenObtenerActivoShouldRejectMissingUsuario();
    }

    @Test
    @DisplayName("Rechaza el usuario suspendido aunque su identificador exista")
    void obtenerActivoShouldRejectSuspendedUsuario() {
        Usuario usuario = usuario();
        usuario.setEstado("SUSPENDIDO");
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));

        thenObtenerActivoShouldRejectSuspendedUsuario();
    }

    // --- arrange ---
    private void givenEmailIsFree() {
        when(contraseniaPort.codificar(CONTRASENIA)).thenReturn(HASH);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario());
    }

    private void givenObtenerActivoShouldReturnActiveUsuario() {
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario()));
    }

    // --- act ---
    private Usuario registrar() {
        return usuarioService.registrar(" " + NOMBRE + " ", " PERSONA@EXAMPLE.COM ", CONTRASENIA, FECHA);
    }

    // --- assert ---
    private void thenSavedPersonaHasEveryField(Usuario result) {
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario saved = captor.getValue();
        assertThat(saved.getId()).isNull();
        assertThat(saved.getNombre()).isEqualTo(NOMBRE);
        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getHashContrasenia()).isEqualTo(HASH);
        assertThat(saved.getTipo()).isEqualTo("PERSONA");
        assertThat(saved.getEstado()).isEqualTo("ACTIVO");
        assertThat(saved.getCreadoEn()).isEqualTo(FECHA);
        assertThat(saved.getActualizadoEn()).isEqualTo(FECHA);
        assertThat(result.getId()).isEqualTo(USUARIO_ID);
    }

    private void thenRegistrarShouldSaveNormalizedPersona(Usuario result) {
        thenSavedPersonaHasEveryField(result);
    }

    private void thenRegistrarShouldRejectDuplicateBeforeEncoding() {
        assertThatThrownBy(this::registrar).isInstanceOf(DuplicateResourceException.class);
        verifyNoInteractions(contraseniaPort);
    }

    private void thenObtenerActivoShouldReturnActiveUsuario(Usuario result) {
        assertThat(result.getId()).isEqualTo(USUARIO_ID);
    }

    private void thenObtenerActivoShouldRejectMissingUsuario() {
        assertThatThrownBy(() -> usuarioService.obtenerActivo(USUARIO_ID))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    private void thenObtenerActivoShouldRejectSuspendedUsuario() {
        assertThatThrownBy(() -> usuarioService.obtenerActivo(USUARIO_ID))
                .isInstanceOf(CredencialesInvalidasException.class);
    }
}
