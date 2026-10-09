package com.xpedia.backend.domain.model.inscripcion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoNodo {

    private UUID inscripcionId;

    private UUID nodoId;

    private String estado;

    private BigDecimal dominio;

    private Short nivel;

    private Integer cantidadFallos;

    private OffsetDateTime creadoEn;

    private OffsetDateTime actualizadoEn;

    public static ProgresoNodo inicial(UUID inscripcionId, UUID nodoId, boolean tienePrerrequisitos,
                                      OffsetDateTime fecha) {
        return ProgresoNodo.builder()
                .inscripcionId(inscripcionId)
                .nodoId(nodoId)
                .estado(tienePrerrequisitos ? "BLOQUEADO" : "DISPONIBLE")
                .dominio(BigDecimal.ZERO)
                .nivel((short) 0)
                .cantidadFallos(0)
                .creadoEn(fecha)
                .actualizadoEn(fecha)
                .build();
    }
}

