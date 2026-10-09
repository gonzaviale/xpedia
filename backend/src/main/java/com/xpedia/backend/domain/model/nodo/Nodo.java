package com.xpedia.backend.domain.model.nodo;

import lombok.*;
import java.util.UUID;
import java.time.OffsetDateTime;
import java.util.List;
import com.xpedia.backend.domain.model.enums.TipoNodo;

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
