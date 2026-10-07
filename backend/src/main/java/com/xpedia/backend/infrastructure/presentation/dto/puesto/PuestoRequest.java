package com.xpedia.backend.infrastructure.presentation.dto.puesto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PuestoRequest(
        /** null = puesto global de Xpedia. Se ignora al actualizar. */
        UUID organizacionId,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre
) {
}
