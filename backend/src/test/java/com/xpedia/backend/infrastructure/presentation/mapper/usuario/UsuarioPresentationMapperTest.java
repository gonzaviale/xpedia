package com.xpedia.backend.infrastructure.presentation.mapper.usuario;

import com.xpedia.backend.domain.dto.usuario.RegistrarUsuarioRequest;
import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.RegistrarUsuarioWebRequest;
import com.xpedia.backend.infrastructure.presentation.dto.usuario.UsuarioResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.UsuarioTestData.CONTRASENIA;
import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static org.assertj.core.api.Assertions.assertThat;

class UsuarioPresentationMapperTest {

    private final UsuarioPresentationMapper usuarioPresentationMapper = new UsuarioPresentationMapper();

    @Test
    @DisplayName("Pasa nombre, email y contraseña al request de dominio")
    void toRequestShouldMapEveryInputField() {
        RegistrarUsuarioRequest result = usuarioPresentationMapper.toRequest(
                new RegistrarUsuarioWebRequest(NOMBRE, EMAIL, CONTRASENIA));

        thenToRequestShouldMapEveryInputField(result);
    }

    @Test
    @DisplayName("Devuelve solo los campos públicos del usuario")
    void toResponseShouldMapEveryPublicField() {
        UsuarioResponse result = usuarioPresentationMapper.toResponse(
                new UsuarioItem(USUARIO_ID, NOMBRE, EMAIL, "PERSONA"));

        thenToResponseShouldMapEveryPublicField(result);
    }

    // --- assert ---
    private void thenToRequestShouldMapEveryInputField(RegistrarUsuarioRequest result) {
        assertThat(result.nombre()).isEqualTo(NOMBRE);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.contrasenia()).isEqualTo(CONTRASENIA);
    }

    private void thenToResponseShouldMapEveryPublicField(UsuarioResponse result) {
        assertThat(result.id()).isEqualTo(USUARIO_ID);
        assertThat(result.nombre()).isEqualTo(NOMBRE);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.tipo()).isEqualTo("PERSONA");
    }
}
