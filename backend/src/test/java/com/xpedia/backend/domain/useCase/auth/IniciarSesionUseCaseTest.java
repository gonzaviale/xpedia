package com.xpedia.backend.domain.useCase.auth;

import com.xpedia.backend.domain.dto.auth.IniciarSesionRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.mapper.auth.IniciarSesionMapper;
import com.xpedia.backend.domain.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IniciarSesionUseCaseTest {

    private AuthService authService;

    private IniciarSesionUseCase iniciarSesionUseCase;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        iniciarSesionUseCase = new IniciarSesionUseCase(authService, new IniciarSesionMapper());
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
        when(authService.autenticar(EMAIL, CONTRASENIA)).thenThrow(new CredencialesInvalidasException());

        thenExecuteShouldPropagateDomainException();
    }

    // --- arrange ---
    private void givenExecuteShouldDelegateAndReturnPublicUsuario() {
        when(authService.autenticar(EMAIL, CONTRASENIA)).thenReturn(usuario());
    }

    // --- act ---
    private UsuarioItem ejecutar() {
        return iniciarSesionUseCase.execute(new IniciarSesionRequest(EMAIL, CONTRASENIA));
    }

    // --- assert ---
    private void thenExecuteShouldDelegateAndReturnPublicUsuario(UsuarioItem result) {
        assertThat(result.id()).isEqualTo(USUARIO_ID);
        assertThat(result.nombre()).isEqualTo(NOMBRE);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.tipo()).isEqualTo("PERSONA");
        verify(authService).autenticar(EMAIL, CONTRASENIA);
    }

    private void thenExecuteShouldPropagateDomainException() {
        assertThatThrownBy(this::ejecutar).isInstanceOf(CredencialesInvalidasException.class);
    }
}
