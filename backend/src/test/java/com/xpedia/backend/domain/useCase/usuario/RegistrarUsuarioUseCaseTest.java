package com.xpedia.backend.domain.useCase.usuario;

import com.xpedia.backend.domain.dto.usuario.RegistrarUsuarioRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.mapper.usuario.RegistrarUsuarioMapper;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.ZoneOffset;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.FECHA;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrarUsuarioUseCaseTest {

    private UsuarioService usuarioService;

    private RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        registrarUsuarioUseCase = new RegistrarUsuarioUseCase(usuarioService, new RegistrarUsuarioMapper(),
                Clock.fixed(FECHA.toInstant(), ZoneOffset.UTC));
    }

    @Test
    @DisplayName("Delega los argumentos correctos y devuelve los datos públicos mapeados")
    void executeShouldDelegateAndReturnPublicUsuario() {
        givenExecuteShouldDelegateAndReturnPublicUsuario();
        UsuarioItem result = ejecutar();

        thenExecuteShouldDelegateAndReturnPublicUsuario(result);
    }

    @Test
    @DisplayName("Propaga el error de dominio sin convertirlo")
    void executeShouldPropagateDomainException() {
        when(usuarioService.registrar(NOMBRE, EMAIL, CONTRASENIA,
                FECHA)).thenThrow(new CredencialesInvalidasException());

        thenExecuteShouldPropagateDomainException();
    }

    // --- arrange ---
    private void givenExecuteShouldDelegateAndReturnPublicUsuario() {
        when(usuarioService.registrar(NOMBRE, EMAIL, CONTRASENIA, FECHA)).thenReturn(usuario());
    }

    // --- act ---
    private UsuarioItem ejecutar() {
        return registrarUsuarioUseCase.execute(new RegistrarUsuarioRequest(NOMBRE, EMAIL, CONTRASENIA));
    }

    // --- assert ---
    private void thenExecuteShouldDelegateAndReturnPublicUsuario(UsuarioItem result) {
        assertThat(result.id()).isEqualTo(USUARIO_ID);
        assertThat(result.nombre()).isEqualTo(NOMBRE);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.tipo()).isEqualTo("PERSONA");
        verify(usuarioService).registrar(NOMBRE, EMAIL, CONTRASENIA, FECHA);
    }

    private void thenExecuteShouldPropagateDomainException() {
        assertThatThrownBy(this::ejecutar).isInstanceOf(CredencialesInvalidasException.class);
    }
}
