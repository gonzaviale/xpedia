package com.xpedia.backend.domain.mapper.reto;

import com.xpedia.backend.domain.dto.reto.CriterioRubricaItem;
import com.xpedia.backend.domain.dto.reto.ListarRetosResponse;
import com.xpedia.backend.domain.dto.reto.RetoItem;
import com.xpedia.backend.domain.dto.reto.RubricaItem;
import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.model.rubrica.CriterioRubrica;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListarRetosMapperTest {

    private static final UUID RETO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000001");
    private static final UUID OTRO_RETO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000002");
    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");
    private static final UUID OTRO_CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000002");
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-08T10:00:00-03:00");
    private static final Map<String, Object> CONTENIDO = Map.of("consigna", "Responder ñ", "contexto", "Caso ficticio");

    private ListarRetosMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ListarRetosMapper();
    }

    @Test
    @DisplayName("Mapea cada campo del reto al item")
    void toResponseShouldMapEveryRetoField() {
        Reto reto = reto(RETO_ID);

        ListarRetosResponse response = toResponse(List.of(reto));

        thenItemHasRetoFields(response.content().getFirst());
    }

    @Test
    @DisplayName("Mapea la rúbrica con su máximo calculado")
    void toResponseShouldMapRubricaWithComputedMaximum() {
        Reto reto = reto(RETO_ID);

        ListarRetosResponse response = toResponse(List.of(reto));

        thenRubricaHasFields(response.content().getFirst().rubrica());
    }

    @Test
    @DisplayName("Mapea los criterios de la rúbrica en orden")
    void toResponseShouldMapCriteriosInOrder() {
        Reto reto = reto(RETO_ID);

        ListarRetosResponse response = toResponse(List.of(reto));

        thenCriteriosHaveFields(response.content().getFirst().rubrica().criterios());
    }

    @Test
    @DisplayName("Conserva los valores nulos opcionales del reto")
    void toResponseShouldKeepNullOptionalFields() {
        Reto reto = reto(RETO_ID);
        reto.setHitoId(null);
        reto.setRevisadoEn(null);

        ListarRetosResponse response = toResponse(List.of(reto));

        thenOptionalFieldsAreNull(response.content().getFirst());
    }

    @Test
    @DisplayName("Conserva el orden de los retos")
    void toResponseShouldKeepRetosOrder() {
        List<Reto> retos = List.of(reto(OTRO_RETO_ID), reto(RETO_ID));

        ListarRetosResponse response = toResponse(retos);

        assertThat(response.content()).extracting(RetoItem::id).containsExactly(OTRO_RETO_ID, RETO_ID);
    }

    @Test
    @DisplayName("Devuelve contenido vacío cuando no hay retos")
    void toResponseShouldReturnEmptyContentWhenNoRetos() {
        ListarRetosResponse response = toResponse(List.of());

        assertThat(response.content()).isEmpty();
    }

    // --- helpers ---
    private Reto reto(UUID id) {
        return Reto.builder()
                .id(id)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .hitoId(HITO_ID)
                .tipo(TipoReto.DESAFIO_REAL)
                .titulo("Reto 01")
                .nivel((short) 2)
                .contenido(CONTENIDO)
                .origen("HUMANO")
                .revisadoEn(REVISADO_EN)
                .rubrica(rubrica())
                .build();
    }

    private Rubrica rubrica() {
        CriterioRubrica claridad = new CriterioRubrica(
                CRITERIO_ID, (short) 1, "Claridad", "Detalle", (short) 3, new BigDecimal("0.50"), false);
        CriterioRubrica politica = new CriterioRubrica(
                OTRO_CRITERIO_ID, (short) 2, "Política", null, (short) 1, new BigDecimal("0.75"), true);
        return new Rubrica(RUBRICA_ID, "Escritura", null, new BigDecimal("2.25"), List.of(claridad, politica));
    }

    // --- act ---
    private ListarRetosResponse toResponse(List<Reto> retos) {
        return mapper.toResponse(retos);
    }

    // --- assert ---
    private void thenItemHasRetoFields(RetoItem item) {
        assertThat(item.id()).isEqualTo(RETO_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.nodoId()).isEqualTo(NODO_ID);
        assertThat(item.hitoId()).isEqualTo(HITO_ID);
        assertThat(item.tipo()).isEqualTo(TipoReto.DESAFIO_REAL);
        assertThat(item.titulo()).isEqualTo("Reto 01");
        assertThat(item.nivel()).isEqualTo((short) 2);
        assertThat(item.contenido()).isEqualTo(CONTENIDO);
        assertThat(item.origen()).isEqualTo("HUMANO");
        assertThat(item.revisadoEn()).isEqualTo(REVISADO_EN);
    }

    private void thenRubricaHasFields(RubricaItem rubrica) {
        assertThat(rubrica.id()).isEqualTo(RUBRICA_ID);
        assertThat(rubrica.nombre()).isEqualTo("Escritura");
        assertThat(rubrica.descripcion()).isNull();
        assertThat(rubrica.puntajeAprobacion()).isEqualByComparingTo("2.25");
        assertThat(rubrica.puntajeMaximo()).isEqualByComparingTo("2.25");
    }

    private void thenCriteriosHaveFields(List<CriterioRubricaItem> criterios) {
        assertThat(criterios).hasSize(2);
        CriterioRubricaItem claridad = criterios.get(0);
        assertThat(claridad.id()).isEqualTo(CRITERIO_ID);
        assertThat(claridad.posicion()).isEqualTo((short) 1);
        assertThat(claridad.nombre()).isEqualTo("Claridad");
        assertThat(claridad.descripcion()).isEqualTo("Detalle");
        assertThat(claridad.puntajeMax()).isEqualTo((short) 3);
        assertThat(claridad.peso()).isEqualByComparingTo("0.50");
        assertThat(claridad.eliminatorio()).isFalse();
        CriterioRubricaItem politica = criterios.get(1);
        assertThat(politica.id()).isEqualTo(OTRO_CRITERIO_ID);
        assertThat(politica.posicion()).isEqualTo((short) 2);
        assertThat(politica.nombre()).isEqualTo("Política");
        assertThat(politica.descripcion()).isNull();
        assertThat(politica.puntajeMax()).isEqualTo((short) 1);
        assertThat(politica.peso()).isEqualByComparingTo("0.75");
        assertThat(politica.eliminatorio()).isTrue();
    }

    private void thenOptionalFieldsAreNull(RetoItem item) {
        assertThat(item.hitoId()).isNull();
        assertThat(item.revisadoEn()).isNull();
    }
}
