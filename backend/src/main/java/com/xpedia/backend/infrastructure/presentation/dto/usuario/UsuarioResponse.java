package com.xpedia.backend.infrastructure.presentation.dto.usuario;

import java.util.UUID;

public record UsuarioResponse(UUID id, String nombre, String email, String tipo) {
}
