package com.xpedia.backend.infrastructure.presentation.dto.reto;
import com.xpedia.backend.domain.model.enums.TipoReto;
import java.time.OffsetDateTime;
import java.util.*;
public record RetoResponse(UUID id, UUID rutaId, UUID nodoId, UUID hitoId, TipoReto tipo, String titulo,
                           Short nivel, Map<String, Object> contenido, String origen,
                           OffsetDateTime revisadoEn, RubricaResponse rubrica) {}
