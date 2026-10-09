package com.xpedia.backend.infrastructure.presentation.dto.usuario;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;

public record RegistrarUsuarioWebRequest(
        @NotBlank
        @Size(max = 100) String nombre,
        @NotBlank
        @Email
        @Size(max = 254) String email,
        @NotBlank
        @Size(min = 12, max = 72) String contrasenia) {

    public RegistrarUsuarioWebRequest {
        email = email == null ? null : email.trim();
    }

    @AssertTrue(message = "La contraseña admite como máximo 72 bytes UTF-8")
    public boolean isContraseniaValida() {
        return contrasenia == null || contrasenia.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
