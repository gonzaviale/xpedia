package com.xpedia.backend.domain.mapper.usuario;

import com.xpedia.backend.domain.dto.usuario.UsuarioItem;
import com.xpedia.backend.domain.model.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.xpedia.backend.support.UsuarioTestData.EMAIL;
import static com.xpedia.backend.support.UsuarioTestData.NOMBRE;
import static com.xpedia.backend.support.UsuarioTestData.USUARIO_ID;
import static com.xpedia.backend.support.UsuarioTestData.usuario;
import static org.assertj.core.api.Assertions.assertThat;

class RegistrarUsuarioMapperTest {

    private final RegistrarUsuarioMapper registrarUsuarioMapper = new RegistrarUsuarioMapper();

    @Test
    @DisplayName("Devuelve todos los campos públicos y no expone el hash")
    void toResponseShouldMapPublicFields() {
        UsuarioItem result = registrarUsuarioMapper.toResponse(usuario());

        thenToResponseShouldMapPublicFields(result);
    }

    @Test
    @DisplayName("Conserva los campos públicos nulos")
    void toResponseShouldPreserveNullFields() {
        UsuarioItem result = registrarUsuarioMapper.toResponse(new Usuario());

        thenToResponseShouldPreserveNullFields(result);
    }

    // --- assert ---
    private void thenToResponseShouldMapPublicFields(UsuarioItem result) {
        assertThat(result.id()).isEqualTo(USUARIO_ID);
        assertThat(result.nombre()).isEqualTo(NOMBRE);
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.tipo()).isEqualTo("PERSONA");
    }

    private void thenToResponseShouldPreserveNullFields(UsuarioItem result) {
        assertThat(result.id()).isNull();
        assertThat(result.nombre()).isNull();
        assertThat(result.email()).isNull();
        assertThat(result.tipo()).isNull();
    }
}
