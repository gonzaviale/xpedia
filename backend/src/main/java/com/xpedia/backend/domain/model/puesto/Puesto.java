package com.xpedia.backend.domain.model.puesto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Puesto {

    private UUID id;

    /** null = puesto global de Xpedia; con valor = puesto propio de una empresa. */
    private UUID organizacionId;

    private String nombre;

    private OffsetDateTime creadoEn;

    private OffsetDateTime actualizadoEn;
}
