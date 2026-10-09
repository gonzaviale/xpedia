package com.xpedia.backend.infrastructure.presentation.dto.microleccion;

import java.time.OffsetDateTime;
import java.util.*;

public record MicroleccionResponse(UUID id, UUID rutaId, UUID nodoId, String titulo, Short nivel,
                                  Map<String, Object> contenido, String origen, OffsetDateTime revisadoEn,
                                  List<FuenteMicroleccionResponse> fuentes) {}
