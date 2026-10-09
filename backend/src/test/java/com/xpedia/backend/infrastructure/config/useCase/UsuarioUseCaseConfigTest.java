package com.xpedia.backend.infrastructure.config.useCase;

import com.xpedia.backend.domain.dto.usuario.RegistrarUsuarioRequest;
import com.xpedia.backend.domain.service.usuario.UsuarioService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioUseCaseConfigTest {

    private final UsuarioUseCaseConfig config = new UsuarioUseCaseConfig();

    @Test
    @DisplayName("El reloj de registro usa UTC")
    void clockShouldUseUtc() {
        thenClockShouldUseUtc();
    }

    @Test
    @DisplayName("El wiring de registro pasa la fecha del reloj al servicio y mapea el resultado")
    void registrarUsuarioUseCaseShouldWireClockServiceAndMapper() {
        UsuarioService service = mock(UsuarioService.class);
        when(service.registrar(NOMBRE, EMAIL, CONTRASENIA, FECHA)).thenReturn(usuario());

        thenRegistrarUsuarioUseCaseShouldWireClockServiceAndMapper(service);
    }

    // --- assert ---
    private void thenClockShouldUseUtc() {
        assertThat(config.clock().getZone()).isEqualTo(ZoneOffset.UTC);
    }

    private void thenRegistrarUsuarioUseCaseShouldWireClockServiceAndMapper(UsuarioService service) {
        assertThat(config.registrarUsuarioUseCase(service, config.registrarUsuarioMapper(),
                Clock.fixed(FECHA.toInstant(), ZoneOffset.UTC))
                .execute(new RegistrarUsuarioRequest(NOMBRE, EMAIL, CONTRASENIA)).id()).isEqualTo(USUARIO_ID);
    }
}
