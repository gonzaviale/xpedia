package com.xpedia.backend.domain.model.microleccion;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Microleccion {
    private UUID id;
    private UUID rutaId;
    private UUID nodoId;
    private String titulo;
    private Short nivel;
    private Map<String, Object> contenido;
    private String origen;
    private OffsetDateTime revisadoEn;
    private List<FuenteMicroleccion> fuentes;
}
