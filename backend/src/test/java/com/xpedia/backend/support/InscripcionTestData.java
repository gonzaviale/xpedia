package com.xpedia.backend.support;

import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.model.inscripcion.Inscripcion;
import com.xpedia.backend.domain.model.inscripcion.ProgresoNodo;
import com.xpedia.backend.domain.model.inscripcion.RecorridoPersonal;
import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.model.ruta.Ruta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class InscripcionTestData {

    public static final UUID USUARIO_ID = UUID.fromString("b9000000-0000-4000-8000-000000000001");
    public static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    public static final UUID HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    public static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    public static final UUID SEGUNDO_NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000503");
    public static final UUID INSCRIPCION_ID = UUID.fromString("c1000000-0000-4000-8000-000000000001");
    public static final OffsetDateTime FECHA = OffsetDateTime.parse("2026-10-09T10:15:30Z");
    public static final LocalDate LLEGADA = LocalDate.of(2027, 8, 5);
    public static final String SLUG = "sistemas-test";
    public static final String META = "Cambiar a atención remota";

    private InscripcionTestData() {
    }

    public static Inscripcion inscripcion() {
        return Inscripcion.builder()
                .id(INSCRIPCION_ID)
                .usuarioId(USUARIO_ID)
                .rutaId(RUTA_ID)
                .objetivo(ObjetivoRuta.CAMBIAR)
                .metaPersonal(META)
                .ritmoMin((short) 20)
                .fechaLlegadaEstimada(LLEGADA)
                .estado("ACTIVA")
                .hitoActualId(HITO_ID)
                .iniciadaEn(FECHA)
                .creadoEn(FECHA.minusDays(1))
                .actualizadoEn(FECHA.plusDays(1))
                .build();
    }

    public static ProgresoNodo progreso() {
        return ProgresoNodo.builder()
                .inscripcionId(INSCRIPCION_ID)
                .nodoId(NODO_ID)
                .estado("EN_CURSO")
                .dominio(new BigDecimal("0.65"))
                .nivel((short) 2)
                .cantidadFallos(3)
                .creadoEn(FECHA)
                .actualizadoEn(FECHA.plusDays(1))
                .build();
    }

    public static RecorridoPersonal recorrido() {
        return new RecorridoPersonal(inscripcion(), SLUG, List.of(progreso()));
    }

    public static Nodo nodo(UUID id, UUID hitoId, List<UUID> prerrequisitos) {
        return Nodo.builder()
                .id(id)
                .hitoId(hitoId)
                .prerrequisitoIds(prerrequisitos)
                .build();
    }

    public static Hito hito(UUID id) {
        return Hito.builder().id(id).build();
    }

    public static Ruta ruta() {
        return Ruta.builder()
                .id(RUTA_ID)
                .slug(SLUG)
                .horasEstimadas(new BigDecimal("100"))
                .build();
    }
}

