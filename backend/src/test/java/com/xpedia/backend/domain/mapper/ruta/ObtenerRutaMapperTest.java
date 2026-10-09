package com.xpedia.backend.domain.mapper.ruta;

import com.xpedia.backend.domain.dto.ruta.ObtenerRutaResponse;
import com.xpedia.backend.domain.model.enums.EstadoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObtenerRutaMapperTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final String SLUG = "atencion-al-cliente";
    private static final Integer VERSION = 3;
    private static final String TITULO = "Atención al cliente";
    private static final String META = "Atender consultas por chat";
    private static final String PERFIL_INICIAL = "Personas con experiencia";
    private static final String PAIS = "AR";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("10.5");
    private static final Short RITMO_RECOMENDADO_MIN = 30;
    private static final OffsetDateTime REVISADA_EN = OffsetDateTime.parse("2026-03-01T10:15:30Z");
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-02-01T08:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-03-02T09:30:00Z");

    private final ObtenerRutaMapper mapper = new ObtenerRutaMapper();

    @Test
    @DisplayName("Mapea cada campo público de la ruta al detalle de la respuesta")
    void toResponseShouldMapEveryPublicField() {
        ObtenerRutaResponse response = toResponse(ruta());

        thenResponseHasEveryPublicField(response);
    }

    @Test
    @DisplayName("Conserva nulos los campos opcionales ausentes de la ruta")
    void toResponseShouldKeepOptionalFieldsNullWhenRutaHasNone() {
        ObtenerRutaResponse response = toResponse(Ruta.builder().id(RUTA_ID).build());

        thenResponseHasNullOptionalFields(response);
    }

    // --- act ---
    private ObtenerRutaResponse toResponse(Ruta ruta) {
        return mapper.toResponse(ruta);
    }

    // --- assert ---
    private void thenResponseHasEveryPublicField(ObtenerRutaResponse response) {
        assertThat(response.id()).isEqualTo(RUTA_ID);
        assertThat(response.slug()).isEqualTo(SLUG);
        assertThat(response.version()).isEqualTo(VERSION);
        assertThat(response.titulo()).isEqualTo(TITULO);
        assertThat(response.tipo()).isEqualTo(TipoRuta.TECNICA);
        assertThat(response.objetivo()).isEqualTo(ObjetivoRuta.MEJORAR);
        assertThat(response.meta()).isEqualTo(META);
        assertThat(response.perfilInicial()).isEqualTo(PERFIL_INICIAL);
        assertThat(response.pais()).isEqualTo(PAIS);
        assertThat(response.horasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(response.ritmoRecomendadoMin()).isEqualTo(RITMO_RECOMENDADO_MIN);
        assertThat(response.estado()).isEqualTo(EstadoRuta.PUBLICADA);
        assertThat(response.validacion()).isEqualTo(ValidacionRuta.REVISADA);
        assertThat(response.revisadaEn()).isEqualTo(REVISADA_EN);
        assertThat(response.creadoEn()).isEqualTo(CREADO_EN);
        assertThat(response.actualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }

    private void thenResponseHasNullOptionalFields(ObtenerRutaResponse response) {
        assertThat(response.id()).isEqualTo(RUTA_ID);
        assertThat(response.perfilInicial()).isNull();
        assertThat(response.pais()).isNull();
        assertThat(response.horasEstimadas()).isNull();
        assertThat(response.revisadaEn()).isNull();
    }

    // --- helpers ---
    private Ruta ruta() {
        return Ruta.builder()
                .id(RUTA_ID)
                .slug(SLUG)
                .version(VERSION)
                .titulo(TITULO)
                .tipo(TipoRuta.TECNICA)
                .objetivo(ObjetivoRuta.MEJORAR)
                .meta(META)
                .perfilInicial(PERFIL_INICIAL)
                .pais(PAIS)
                .horasEstimadas(HORAS_ESTIMADAS)
                .ritmoRecomendadoMin(RITMO_RECOMENDADO_MIN)
                .estado(EstadoRuta.PUBLICADA)
                .validacion(ValidacionRuta.REVISADA)
                .revisadaEn(REVISADA_EN)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }
}
