package com.xpedia.backend.domain.model.microleccion;

import java.util.UUID;

public record FuenteMicroleccion(UUID id, String titulo, String url, String licencia,
                                String uso, boolean permiteUsoComercial, String ubicacion) {}
