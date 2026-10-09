package com.xpedia.backend.domain.model.inscripcion;

import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion {

    private UUID id;

    private UUID usuarioId;

    private UUID rutaId;

    private ObjetivoRuta objetivo;

    private String metaPersonal;

    private Short ritmoMin;

    private LocalDate fechaLlegadaEstimada;

    private String estado;

    private UUID hitoActualId;

    private OffsetDateTime iniciadaEn;

    private OffsetDateTime creadoEn;

    private OffsetDateTime actualizadoEn;

    public static LocalDate estimarLlegada(BigDecimal horas, int ritmoMin, LocalDate inicio) {
        if (horas == null) {
            return null;
        }
        if (horas.signum() <= 0) {
            throw new BusinessRuleException("La ruta debe declarar una duración positiva");
        }
        long dias = horas.multiply(BigDecimal.valueOf(60))
                .divide(BigDecimal.valueOf(ritmoMin), 0, RoundingMode.CEILING)
                .longValueExact();
        return inicio.plusDays(dias);
    }

    public static String normalizarMeta(String metaPersonal) {
        return metaPersonal == null ? null : metaPersonal.trim();
    }
}

