package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.dto.auth.IniciarSesionRequest;
import com.xpedia.backend.domain.dto.auth.ObtenerUsuarioActualRequest;
import com.xpedia.backend.domain.service.auth.AuthService;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthUseCaseConfigTest {

    private final AuthUseCaseConfig config = new AuthUseCaseConfig();

    @Test
    @DisplayName("El wiring de login autentica con el servicio y entrega el resultado mapeado")
    void iniciarSesionUseCaseShouldWireServiceAndMapper() {
        AuthService service = mock(AuthService.class);
        when(service.autenticar(EMAIL, CONTRASENIA)).thenReturn(usuario());

        thenIniciarSesionUseCaseShouldWireServiceAndMapper(service);
    }

    @Test
    @DisplayName("El wiring de usuario actual delega su identidad al servicio correcto")
    void obtenerUsuarioActualUseCaseShouldWireServiceAndMapper() {
        UsuarioService service = mock(UsuarioService.class);
        when(service.obtenerActivo(USUARIO_ID)).thenReturn(usuario());

        thenObtenerUsuarioActualUseCaseShouldWireServiceAndMapper(service);
    }

    // --- assert ---
    private void thenIniciarSesionUseCaseShouldWireServiceAndMapper(AuthService service) {
        assertThat(config.iniciarSesionUseCase(service, config.iniciarSesionMapper())
                .execute(new IniciarSesionRequest(EMAIL, CONTRASENIA)).id()).isEqualTo(USUARIO_ID);
    }

    private void thenObtenerUsuarioActualUseCaseShouldWireServiceAndMapper(UsuarioService service) {
        assertThat(config.obtenerUsuarioActualUseCase(service, config.obtenerUsuarioActualMapper())
                .execute(new ObtenerUsuarioActualRequest(USUARIO_ID)).id()).isEqualTo(USUARIO_ID);
    }
}
