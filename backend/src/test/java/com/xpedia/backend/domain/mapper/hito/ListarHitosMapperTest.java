package com.xpedia.backend.domain.mapper.hito;

import com.xpedia.backend.domain.dto.hito.HitoItem;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.domain.model.hito.Hito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListarHitosMapperTest {

    private static final UUID PRIMER_ID = UUID.randomUUID();
    private static final UUID SEGUNDO_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final Short PRIMERA_POSICION = 1;
    private static final String TITULO = "Primer hito";
    private static final String OBJETIVO = "Aprender lo básico";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("8.5");
    private static final String EVIDENCIA_ESPERADA = "Un informe";

    private ListarHitosMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ListarHitosMapper();
    }

    @Test
    @DisplayName("Mapea cada campo del hito al item")
    void toResponseShouldMapEveryHitoField() {
        List<Hito> hitos = List.of(hitoCompleto());

        ListarHitosResponse response = mapear(hitos);

        thenItemHasEveryHitoField(response.content().getFirst());
    }

    @Test
    @DisplayName("Conserva el orden de los hitos")
    void toResponseShouldKeepHitosOrder() {
        List<Hito> hitos = List.of(hitoConId(PRIMER_ID), hitoConId(SEGUNDO_ID));

        ListarHitosResponse response = mapear(hitos);

        thenItemIdsAre(response, PRIMER_ID, SEGUNDO_ID);
    }

    @Test
    @DisplayName("Conserva los nulos de un hito sin datos")
    void toResponseShouldKeepNullsWhenHitoHasNoData() {
        List<Hito> hitos = List.of(new Hito());

        ListarHitosResponse response = mapear(hitos);

        thenItemHasOnlyNulls(response.content().getFirst());
    }

    @Test
    @DisplayName("Devuelve contenido vacío cuando no hay hitos")
    void toResponseShouldReturnEmptyContentWhenThereAreNoHitos() {
        ListarHitosResponse response = mapear(List.of());

        assertThat(response.content()).isEmpty();
    }

    // --- helpers ---
    private Hito hitoCompleto() {
        return Hito.builder()
                .id(PRIMER_ID)
                .rutaId(RUTA_ID)
                .posicion(PRIMERA_POSICION)
                .titulo(TITULO)
                .objetivo(OBJETIVO)
                .horasEstimadas(HORAS_ESTIMADAS)
                .esFinal(true)
                .evidenciaEsperada(EVIDENCIA_ESPERADA)
                .build();
    }

    private Hito hitoConId(UUID id) {
        return Hito.builder().id(id).build();
    }

    // --- act ---
    private ListarHitosResponse mapear(List<Hito> hitos) {
        return mapper.toResponse(hitos);
    }

    // --- assert ---
    private void thenItemHasEveryHitoField(HitoItem item) {
        assertThat(item.id()).isEqualTo(PRIMER_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.posicion()).isEqualTo(PRIMERA_POSICION);
        assertThat(item.titulo()).isEqualTo(TITULO);
        assertThat(item.objetivo()).isEqualTo(OBJETIVO);
        assertThat(item.horasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(item.esFinal()).isTrue();
        assertThat(item.evidenciaEsperada()).isEqualTo(EVIDENCIA_ESPERADA);
    }

    private void thenItemIdsAre(ListarHitosResponse response, UUID first, UUID second) {
        assertThat(response.content()).extracting(HitoItem::id).containsExactly(first, second);
    }

    private void thenItemHasOnlyNulls(HitoItem item) {
        assertThat(item.id()).isNull();
        assertThat(item.rutaId()).isNull();
        assertThat(item.posicion()).isNull();
        assertThat(item.titulo()).isNull();
        assertThat(item.objetivo()).isNull();
        assertThat(item.horasEstimadas()).isNull();
        assertThat(item.esFinal()).isNull();
        assertThat(item.evidenciaEsperada()).isNull();
    }
}
