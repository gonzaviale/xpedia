package com.xpedia.backend.domain.dto.usuario;

import java.util.UUID;

public record UsuarioItem(UUID id, String nombre, String email, String tipo) {
}
