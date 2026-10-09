package com.xpedia.backend.domain.useCase.auth;

import com.xpedia.backend.domain.dto.auth.ObtenerUsuarioActualRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.exception.CredencialesInvalidasException;
import com.xpedia.backend.domain.mapper.auth.ObtenerUsuarioActualMapper;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ObtenerUsuarioActualUseCaseTest {

    private UsuarioService usuarioService;

    private ObtenerUsuarioActualUseCase obtenerUsuarioActualUseCase;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        obtenerUsuarioActualUseCase = new ObtenerUsuarioActualUseCase(usuarioService, new ObtenerUsuarioActualMapper());
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
        when(usuarioService.obtenerActivo(USUARIO_ID)).thenThrow(new CredencialesInvalidasException());

        thenExecuteShouldPropagateDomainException();
    }

    // --- arrange ---
    private void givenExecuteShouldDelegateAndReturnPublicUsuario() {
        when(usuarioService.obtenerActivo(USUARIO_ID)).thenReturn(usuario());
    }

    // --- act ---
    private UsuarioItem ejecutar() {
        return obtenerUsuarioActualUseCase.execute(new ObtenerUsuarioActualRequest(USUARIO_ID));
    }

    // --- assert ---
    private void thenExecuteShouldDelegateAndReturnPublicUsuario(UsuarioItem result) {
        assertThat(result.id()).isEqualTo(USUARIO_ID);
        assertThat(result.nombre()).isEqualTo(NOMBRE);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.tipo()).isEqualTo("PERSONA");
        verify(usuarioService).obtenerActivo(USUARIO_ID);
    }

    private void thenExecuteShouldPropagateDomainException() {
        assertThatThrownBy(this::ejecutar).isInstanceOf(CredencialesInvalidasException.class);
    }
}
