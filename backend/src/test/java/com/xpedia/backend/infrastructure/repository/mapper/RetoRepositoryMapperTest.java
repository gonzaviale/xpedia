package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RetoRepositoryMapperTest {

    private static final UUID RETO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000001");
    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-08T10:00:00-03:00");

    private RetoRepositoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RetoRepositoryMapper();
    }

    @Test
    @DisplayName("Mapea cada campo de la actividad al reto")
    void toDomainShouldMapEveryActividadField() {
        ActividadEntity entity = actividad("ENSAYO", Map.of("consigna", "Texto"));

        Reto reto = toDomain(entity, rubrica());

        thenRetoHasActividadFields(reto);
    }

    @ParameterizedTest
    @EnumSource(TipoReto.class)
    @DisplayName("Convierte el tipo de la actividad al enum TipoReto")
    void toDomainShouldConvertTipoToEnum(TipoReto tipo) {
        ActividadEntity entity = actividad(tipo.name(), Map.of("consigna", "Texto"));

        Reto reto = toDomain(entity, rubrica());

        assertThat(reto.getTipo()).isEqualTo(tipo);
    }

    @Test
    @DisplayName("Asigna al reto la rúbrica recibida")
    void toDomainShouldAssignGivenRubrica() {
        Rubrica rubrica = rubrica();

        Reto reto = toDomain(actividad("ENSAYO", Map.of("consigna", "Texto")), rubrica);

        assertThat(reto.getRubrica()).isSameAs(rubrica);
    }

    @Test
    @DisplayName("Expone solo las claves públicas del contenido y oculta soluciones y configuración")
    void toDomainShouldExposeOnlyPublicContentKeys() {
        ActividadEntity entity = actividad("ENSAYO", Map.of(
                "consigna", "Texto",
                "contexto", "Situación",
                "formatoEntrega", "Escrito",
                "respuestaEsperada", "SECRETO",
                "evaluador", Map.of("clave", "SECRETO")));

        Reto reto = toDomain(entity, rubrica());

        assertThat(reto.getContenido()).containsOnlyKeys("consigna", "contexto", "formatoEntrega");
    }

    @Test
    @DisplayName("Omite las claves públicas cuyo valor no es texto")
    void toDomainShouldOmitNonTextualPublicKeys() {
        ActividadEntity entity = actividad("ENSAYO", Map.of(
                "consigna", "Texto",
                "contexto", Map.of("secreto", "dato"),
                "formatoEntrega", 17));

        Reto reto = toDomain(entity, rubrica());

        assertThat(reto.getContenido()).containsOnlyKeys("consigna");
    }

    @Test
    @DisplayName("Omite las claves públicas ausentes del contenido")
    void toDomainShouldOmitAbsentPublicKeys() {
        ActividadEntity entity = actividad("ENSAYO", Map.of("consigna", "Texto"));

        Reto reto = toDomain(entity, rubrica());

        assertThat(reto.getContenido()).containsOnlyKeys("consigna");
    }

    @Test
    @DisplayName("Conserva los valores nulos opcionales de la actividad")
    void toDomainShouldKeepNullOptionalFields() {
        ActividadEntity entity = actividad("ENSAYO", Map.of("consigna", "Texto"));
        entity.setHitoId(null);
        entity.setRevisadoEn(null);

        Reto reto = toDomain(entity, rubrica());

        assertThat(reto.getHitoId()).isNull();
        assertThat(reto.getRevisadoEn()).isNull();
    }

    // --- helpers ---
    private ActividadEntity actividad(String tipo, Map<String, Object> contenido) {
        return ActividadEntity.builder()
                .id(RETO_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .hitoId(HITO_ID)
                .rubricaId(RUBRICA_ID)
                .tipo(tipo)
                .titulo("Reto 01")
                .nivel((short) 2)
                .contenido(contenido)
                .origen("HUMANO")
                .estadoRevision("APROBADA")
                .revisadoEn(REVISADO_EN)
                .build();
    }

    private Rubrica rubrica() {
        return new Rubrica(RUBRICA_ID, "Escritura", null, new BigDecimal("2.25"), List.of());
    }

    // --- act ---
    private Reto toDomain(ActividadEntity entity, Rubrica rubrica) {
        return mapper.toDomain(entity, rubrica);
    }

    // --- assert ---
    private void thenRetoHasActividadFields(Reto reto) {
        assertThat(reto.getId()).isEqualTo(RETO_ID);
        assertThat(reto.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(reto.getNodoId()).isEqualTo(NODO_ID);
        assertThat(reto.getHitoId()).isEqualTo(HITO_ID);
        assertThat(reto.getTipo()).isEqualTo(TipoReto.ENSAYO);
        assertThat(reto.getTitulo()).isEqualTo("Reto 01");
        assertThat(reto.getNivel()).isEqualTo((short) 2);
        assertThat(reto.getContenido()).isEqualTo(Map.of("consigna", "Texto"));
        assertThat(reto.getOrigen()).isEqualTo("HUMANO");
        assertThat(reto.getRevisadoEn()).isEqualTo(REVISADO_EN);
    }
}
