package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.enums.EstadoPropuestaHito;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.infrastructure.repository.entity.HitoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HitoRepositoryMapperTest {

    private static final UUID HITO_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final Short POSICION = 2;
    private static final String TITULO = "Primer hito";
    private static final String OBJETIVO = "Aprender lo básico";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("8.5");
    private static final String EVIDENCIA_ESPERADA = "Un informe";
    private static final String COMENTARIO_AJUSTE = "Acortar el hito";
    private static final OffsetDateTime CREADO_EN = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final OffsetDateTime ACTUALIZADO_EN = OffsetDateTime.parse("2026-01-02T10:00:00Z");

    private HitoRepositoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HitoRepositoryMapper();
    }

    @Test
    @DisplayName("Mapea todos los campos de la entidad al modelo")
    void toDomainShouldMapEveryEntityField() {
        Hito result = mapper.toDomain(hitoEntity());

        thenHitoHasEveryEntityField(result);
    }

    @Test
    @DisplayName("Conserva los nulos de una entidad sin datos")
    void toDomainShouldKeepNullsWhenEntityHasNoData() {
        Hito result = mapper.toDomain(new HitoEntity());

        thenHitoHasOnlyNulls(result);
    }

    // --- helpers ---
    private HitoEntity hitoEntity() {
        return HitoEntity.builder()
                .id(HITO_ID)
                .rutaId(RUTA_ID)
                .posicion(POSICION)
                .titulo(TITULO)
                .objetivo(OBJETIVO)
                .horasEstimadas(HORAS_ESTIMADAS)
                .esFinal(true)
                .evidenciaEsperada(EVIDENCIA_ESPERADA)
                .estadoPropuesta(EstadoPropuestaHito.A_AJUSTAR)
                .comentarioAjuste(COMENTARIO_AJUSTE)
                .creadoEn(CREADO_EN)
                .actualizadoEn(ACTUALIZADO_EN)
                .build();
    }

    // --- assert ---
    private void thenHitoHasEveryEntityField(Hito hito) {
        assertThat(hito.getId()).isEqualTo(HITO_ID);
        assertThat(hito.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(hito.getPosicion()).isEqualTo(POSICION);
        assertThat(hito.getTitulo()).isEqualTo(TITULO);
        assertThat(hito.getObjetivo()).isEqualTo(OBJETIVO);
        assertThat(hito.getHorasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(hito.getEsFinal()).isTrue();
        assertThat(hito.getEvidenciaEsperada()).isEqualTo(EVIDENCIA_ESPERADA);
        assertThat(hito.getEstadoPropuesta()).isEqualTo(EstadoPropuestaHito.A_AJUSTAR);
        assertThat(hito.getComentarioAjuste()).isEqualTo(COMENTARIO_AJUSTE);
        assertThat(hito.getCreadoEn()).isEqualTo(CREADO_EN);
        assertThat(hito.getActualizadoEn()).isEqualTo(ACTUALIZADO_EN);
    }

    private void thenHitoHasOnlyNulls(Hito hito) {
        assertThat(hito.getId()).isNull();
        assertThat(hito.getRutaId()).isNull();
        assertThat(hito.getPosicion()).isNull();
        assertThat(hito.getTitulo()).isNull();
        assertThat(hito.getObjetivo()).isNull();
        assertThat(hito.getHorasEstimadas()).isNull();
        assertThat(hito.getEsFinal()).isNull();
        assertThat(hito.getEvidenciaEsperada()).isNull();
        assertThat(hito.getEstadoPropuesta()).isNull();
        assertThat(hito.getComentarioAjuste()).isNull();
        assertThat(hito.getCreadoEn()).isNull();
        assertThat(hito.getActualizadoEn()).isNull();
    }
}
