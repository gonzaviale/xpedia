package com.xpedia.backend.infrastructure.presentation.mapper.auth;

import com.xpedia.backend.domain.dto.auth.IniciarSesionRequest;
import com.xpedia.backend.domain.dto.auth.ObtenerUsuarioActualRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.infrastructure.presentation.dto.auth.IniciarSesionWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.UsuarioResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static org.assertj.core.api.Assertions.assertThat;

class AuthPresentationMapperTest {

    private final AuthPresentationMapper authPresentationMapper = new AuthPresentationMapper();

    @Test
    @DisplayName("Pasa email y contraseña al request de dominio")
    void toRequestShouldMapEveryInputField() {
        IniciarSesionRequest result = authPresentationMapper.toRequest(
                new IniciarSesionWebRequest(EMAIL, CONTRASENIA));

        thenToRequestShouldMapEveryInputField(result);
    }

    @Test
    @DisplayName("Obtiene el identificador del usuario actual de la identidad autenticada")
    void toActualRequestShouldMapUsuarioId() {
        ObtenerUsuarioActualRequest result = authPresentationMapper.toActualRequest(USUARIO_ID);

        thenToActualRequestShouldMapUsuarioId(result);
    }

    @Test
    @DisplayName("Devuelve todos los campos públicos del usuario")
    void toResponseShouldMapEveryPublicField() {
        UsuarioResponse result = authPresentationMapper.toResponse(
                new UsuarioItem(USUARIO_ID, NOMBRE, EMAIL, "PERSONA"));

        thenToResponseShouldMapEveryPublicField(result);
    }

    // --- assert ---
    private void thenToRequestShouldMapEveryInputField(IniciarSesionRequest result) {
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.contrasenia()).isEqualTo(CONTRASENIA);
    }

    private void thenToActualRequestShouldMapUsuarioId(ObtenerUsuarioActualRequest result) {
        assertThat(result.usuarioId()).isEqualTo(USUARIO_ID);
    }

    private void thenToResponseShouldMapEveryPublicField(UsuarioResponse result) {
        assertThat(result.id()).isEqualTo(USUARIO_ID);
        assertThat(result.nombre()).isEqualTo(NOMBRE);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.tipo()).isEqualTo("PERSONA");
    }
}
