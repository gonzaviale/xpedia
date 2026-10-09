package com.xpedia.backend.domain.model.microleccion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
