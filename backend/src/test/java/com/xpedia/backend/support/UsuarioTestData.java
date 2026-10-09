package com.xpedia.backend.support;

import com.xpedia.backend.domain.model.usuario.Usuario;
import com.xpedia.backend.infrastructure.repository.entity.UsuarioEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class UsuarioTestData {

    public static final UUID USUARIO_ID = UUID.fromString("b9000000-0000-4000-8000-000000000001");
    public static final String NOMBRE = "Persona";
    public static final String EMAIL = "persona@example.com";
    public static final String CONTRASENIA = "Una contraseña segura";
    public static final String HASH = "{bcrypt}hash-de-prueba";
    public static final OffsetDateTime FECHA = OffsetDateTime.parse("2026-10-09T12:00:00Z");

    private UsuarioTestData() {
    }

    public static Usuario usuario() {
        return Usuario.builder()
                .id(USUARIO_ID)
                .nombre(NOMBRE)
                .email(EMAIL)
                .hashContrasenia(HASH)
                .tipo("PERSONA")
                .estado("ACTIVO")
                .creadoEn(FECHA)
                .actualizadoEn(FECHA)
                .build();
    }

    public static UsuarioEntity entity() {
        return UsuarioEntity.builder()
                .id(USUARIO_ID)
                .nombre(NOMBRE)
                .email(EMAIL)
                .hashContrasenia(HASH)
                .tipo("PERSONA")
                .estado("ACTIVO")
                .creadoEn(FECHA)
                .actualizadoEn(FECHA)
                .build();
    }
}
