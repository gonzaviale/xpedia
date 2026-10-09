package com.xpedia.backend.domain.model.hito;

import lombok.*;
import java.util.UUID;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import com.xpedia.backend.domain.model.enums.EstadoPropuestaHito;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hito {
    private UUID id;

    private UUID rutaId;

    private Short posicion;

    private String titulo;

    private String objetivo;

    private BigDecimal horasEstimadas;

    private Boolean esFinal;

    private String evidenciaEsperada;

    private EstadoPropuestaHito estadoPropuesta;

    private String comentarioAjuste;

    private OffsetDateTime creadoEn;

    private OffsetDateTime actualizadoEn;
}
