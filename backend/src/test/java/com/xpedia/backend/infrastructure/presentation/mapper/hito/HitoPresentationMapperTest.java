package com.xpedia.backend.infrastructure.presentation.mapper.hito;

import com.xpedia.backend.domain.dto.hito.HitoItem;
import com.xpedia.backend.domain.dto.hito.ListarHitosRequest;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.infrastructure.presentation.dto.hito.HitoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HitoPresentationMapperTest {

    private static final UUID HITO_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final Short POSICION = 1;
    private static final String TITULO = "Primer hito";
    private static final String OBJETIVO = "Aprender lo básico";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("8.5");
    private static final String EVIDENCIA_ESPERADA = "Un informe";

    private HitoPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HitoPresentationMapper();
    }

    @Test
    @DisplayName("Construye el request con el id de la ruta")
    void toRequestShouldBuildRequestWithRutaId() {
        ListarHitosRequest request = mapper.toRequest(RUTA_ID);

        assertThat(request.rutaId()).isEqualTo(RUTA_ID);
    }

    @Test
    @DisplayName("Mapea cada campo del item a la respuesta")
    void toResponseShouldMapEveryItemField() {
        ListarHitosResponse response = new ListarHitosResponse(List.of(hitoItem()));

        List<HitoResponse> result = mapper.toResponse(response);

        thenResponseHasEveryItemField(result);
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando no hay hitos")
    void toResponseShouldReturnEmptyListWhenThereAreNoHitos() {
        List<HitoResponse> result = mapper.toResponse(new ListarHitosResponse(List.of()));

        assertThat(result).isEmpty();
    }

    // --- helpers ---
    private HitoItem hitoItem() {
        return new HitoItem(
                HITO_ID,
                RUTA_ID,
                POSICION,
                TITULO,
                OBJETIVO,
                HORAS_ESTIMADAS,
                true,
                EVIDENCIA_ESPERADA);
    }

    // --- assert ---
    private void thenResponseHasEveryItemField(List<HitoResponse> result) {
        assertThat(result).hasSize(1);
        HitoResponse hito = result.getFirst();
        assertThat(hito.id()).isEqualTo(HITO_ID);
        assertThat(hito.rutaId()).isEqualTo(RUTA_ID);
        assertThat(hito.posicion()).isEqualTo(POSICION);
        assertThat(hito.titulo()).isEqualTo(TITULO);
        assertThat(hito.objetivo()).isEqualTo(OBJETIVO);
        assertThat(hito.horasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(hito.esFinal()).isTrue();
        assertThat(hito.evidenciaEsperada()).isEqualTo(EVIDENCIA_ESPERADA);
    }
}
