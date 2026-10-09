package com.xpedia.backend.domain.dto.microleccion;

import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import java.time.OffsetDateTime;
import java.util.*;

public record MicroleccionItem(UUID id, UUID rutaId, UUID nodoId, String titulo, Short nivel,
                              Map<String, Object> contenido, String origen, OffsetDateTime revisadoEn,
                              List<FuenteMicroleccion> fuentes) {}
