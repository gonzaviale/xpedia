package com.xpedia.backend.domain.model.intento;

import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.model.enums.ModoIntento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Intento {

    public static final BigDecimal PUNTAJE_APROBACION = new BigDecimal("80");

    private static final BigDecimal CIEN = new BigDecimal("100");

    private UUID id;

    private UUID usuarioId;

    private UUID inscripcionId;

    private UUID actividadId;

    private ModoIntento modo;

    private BigDecimal puntaje;

    private Boolean aprobado;

    private OffsetDateTime iniciadoEn;

    private OffsetDateTime terminadoEn;

    private List<Respuesta> respuestas;

    public static Intento corregir(Cuestionario cuestionario,
                                   UUID usuarioId,
                                   UUID inscripcionId,
                                   List<Respuesta> enviadas,
                                   OffsetDateTime iniciadoEn,
                                   OffsetDateTime terminadoEn) {
        Map<UUID, Respuesta> enviadasPorPregunta = enviadas.stream()
                .collect(Collectors.toMap(Respuesta::getPreguntaId, Function.identity()));
        List<Respuesta> corregidas = cuestionario.getPreguntas().stream()
                .map(pregunta -> enviadasPorPregunta.get(pregunta.getId()).corregir(pregunta))
                .toList();
        BigDecimal puntaje = calcularPuntaje(corregidas);
        return Intento.builder()
                .usuarioId(usuarioId)
                .inscripcionId(inscripcionId)
                .actividadId(cuestionario.getId())
                .modo(ModoIntento.PRACTICA)
                .puntaje(puntaje)
                .aprobado(puntaje.compareTo(PUNTAJE_APROBACION) >= 0)
                .iniciadoEn(iniciadoEn == null ? terminadoEn : iniciadoEn)
                .terminadoEn(terminadoEn)
                .respuestas(corregidas)
                .build();
    }

    public int cantidadCorrectas() {
        return (int) respuestas.stream().filter(Respuesta::getCorrecta).count();
    }

    public int total() {
        return respuestas.size();
    }

    private static BigDecimal calcularPuntaje(List<Respuesta> corregidas) {
        long correctas = corregidas.stream().filter(Respuesta::getCorrecta).count();
        return BigDecimal.valueOf(correctas)
                .multiply(CIEN)
                .divide(BigDecimal.valueOf(corregidas.size()), 2, RoundingMode.HALF_UP);
    }
}
