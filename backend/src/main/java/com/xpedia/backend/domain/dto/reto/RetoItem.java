package com.xpedia.backend.domain.dto.reto;

import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import java.time.OffsetDateTime;
import java.util.*;

public record RetoItem(UUID id, UUID rutaId, UUID nodoId, UUID hitoId, TipoReto tipo, String titulo,
                       Short nivel, Map<String, Object> contenido, String origen,
                       OffsetDateTime revisadoEn, Rubrica rubrica) {}
