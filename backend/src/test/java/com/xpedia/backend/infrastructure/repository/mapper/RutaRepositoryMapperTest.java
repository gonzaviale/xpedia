package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.enums.EstadoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.infrastructure.repository.entity.RutaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RutaRepositoryMapperTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID ORGANIZACION_ID = UUID.randomUUID();
    private static final String SLUG = "atencion-al-cliente";
    private static final Integer VERSION = 3;
    private static final String TITULO = "Atención al cliente";
    private static final String META = "Atender consultas por chat";
    private static final String PERFIL_INICIAL = "Personas con experiencia";
    private static final String PAIS = "AR";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("10.5");
    private static final Short RITMO_RECOMENDADO_MIN = 30;
    private static final UUID REVISADA_POR = UUID.randomUUID();
    private static final OffsetDateTime REVISADA_EN = OffsetDateTime.parse("2026-03-01T10:15:30Z");
    private static final UUID CONFIRMADA_POR = UUID.randomUUID();
    private static final OffsetDateTime CONFIRMADA_EN = OffsetDateTime.parse("2026-03-01T11:00:00Z");
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-02-01T08:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-03-02T09:30:00Z");

    private final RutaRepositoryMapper mapper = new RutaRepositoryMapper();

    @Test
    @DisplayName("Mapea cada campo de la entidad al modelo de dominio")
    void toDomainShouldMapEveryField() {
        Ruta ruta = toDomain(entity());

        thenRutaHasEveryField(ruta);
    }

    @Test
    @DisplayName("Conserva nulos los campos que la entidad no tiene")
    void toDomainShouldKeepNullsWhenEntityIsEmpty() {
        Ruta ruta = toDomain(new RutaEntity());

        thenRutaHasNoValues(ruta);
    }

    // --- act ---
    private Ruta toDomain(RutaEntity entity) {
        return mapper.toDomain(entity);
    }

    // --- assert ---
    private void thenRutaHasEveryField(Ruta ruta) {
        assertThat(ruta.getId()).isEqualTo(RUTA_ID);
        assertThat(ruta.getOrganizacionId()).isEqualTo(ORGANIZACION_ID);
        assertThat(ruta.getSlug()).isEqualTo(SLUG);
        assertThat(ruta.getVersion()).isEqualTo(VERSION);
        assertThat(ruta.getTitulo()).isEqualTo(TITULO);
        assertThat(ruta.getTipo()).isEqualTo(TipoRuta.HABILIDAD_BLANDA);
        assertThat(ruta.getObjetivo()).isEqualTo(ObjetivoRuta.MEJORAR);
        assertThat(ruta.getMeta()).isEqualTo(META);
        assertThat(ruta.getPerfilInicial()).isEqualTo(PERFIL_INICIAL);
        assertThat(ruta.getPais()).isEqualTo(PAIS);
        assertThat(ruta.getHorasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(ruta.getRitmoRecomendadoMin()).isEqualTo(RITMO_RECOMENDADO_MIN);
        assertThat(ruta.getEstado()).isEqualTo(EstadoRuta.PUBLICADA);
        assertThat(ruta.getValidacion()).isEqualTo(ValidacionRuta.REVISADA);
        assertThat(ruta.getRevisadaPor()).isEqualTo(REVISADA_POR);
        assertThat(ruta.getRevisadaEn()).isEqualTo(REVISADA_EN);
        assertThat(ruta.getConfirmadaPor()).isEqualTo(CONFIRMADA_POR);
        assertThat(ruta.getConfirmadaEn()).isEqualTo(CONFIRMADA_EN);
        assertThat(ruta.getCreadoEn()).isEqualTo(CREADO_EN);
        assertThat(ruta.getActualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }

    private void thenRutaHasNoValues(Ruta ruta) {
        assertThat(ruta).usingRecursiveComparison().isEqualTo(new Ruta());
    }

    // --- helpers ---
    private RutaEntity entity() {
        return RutaEntity.builder()
                .id(RUTA_ID)
                .organizacionId(ORGANIZACION_ID)
                .slug(SLUG)
                .version(VERSION)
                .titulo(TITULO)
                .tipo(TipoRuta.HABILIDAD_BLANDA)
                .objetivo(ObjetivoRuta.MEJORAR)
                .meta(META)
                .perfilInicial(PERFIL_INICIAL)
                .pais(PAIS)
                .horasEstimadas(HORAS_ESTIMADAS)
                .ritmoRecomendadoMin(RITMO_RECOMENDADO_MIN)
                .estado(EstadoRuta.PUBLICADA)
                .validacion(ValidacionRuta.REVISADA)
                .revisadaPor(REVISADA_POR)
                .revisadaEn(REVISADA_EN)
                .confirmadaPor(CONFIRMADA_POR)
                .confirmadaEn(CONFIRMADA_EN)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }
}
