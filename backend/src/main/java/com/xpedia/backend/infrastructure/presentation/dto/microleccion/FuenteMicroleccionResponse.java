package com.xpedia.backend.infrastructure.presentation.dto.microleccion;

import java.util.UUID;

public record FuenteMicroleccionResponse(UUID id, String titulo, String url, String licencia,
                                        String uso, boolean permiteUsoComercial, String ubicacion) {}
