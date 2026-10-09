package com.xpedia.backend.domain.model.nodo;

import com.xpedia.backend.domain.model.enums.TipoNodo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nodo {

    private UUID id;

    private UUID rutaId;

    private UUID hitoId;

    private UUID ramaId;

    private UUID habilidadId;

    private String codigo;

    private String titulo;

    private String resumen;

    private TipoNodo tipo;

    private Short nivel;

    private Short minutosEstimados;

    private List<String> palabrasClave;

    private Short posicion;

    private List<UUID> prerrequisitoIds;

    private OffsetDateTime creadoEn;

    private OffsetDateTime actualizadoEn;
}
