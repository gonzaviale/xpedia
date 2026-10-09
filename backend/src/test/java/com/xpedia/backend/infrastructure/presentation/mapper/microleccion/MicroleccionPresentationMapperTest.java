package com.xpedia.backend.infrastructure.presentation.mapper.microleccion;

import com.xpedia.backend.domain.dto.microleccion.FuenteMicroleccionItem;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesRequest;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesResponse;
import com.xpedia.backend.domain.dto.microleccion.MicroleccionItem;
import com.xpedia.backend.infrastructure.presentation.dto.microleccion.FuenteMicroleccionResponse;
import com.xpedia.backend.infrastructure.presentation.dto.microleccion.MicroleccionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MicroleccionPresentationMapperTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID MICROLECCION_ID = UUID.randomUUID();
    private static final UUID FUENTE_ID = UUID.randomUUID();
    private static final String TITULO = "Lección inicial";
    private static final Short NIVEL = 2;
    private static final Map<String, Object> CONTENIDO = Map.of("texto", "Comunicación ñ");
    private static final String ORIGEN = "HUMANO";
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-01T10:00:00Z");
    private static final String FUENTE_TITULO = "Fuente global";
    private static final String FUENTE_URL = "https://example.org/material";
    private static final String FUENTE_LICENCIA = "Licencia de prueba";
    private static final String FUENTE_USO = "ADAPTABLE";
    private static final String FUENTE_UBICACION = "Sección 1";

    private MicroleccionPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MicroleccionPresentationMapper();
    }

    @Test
    @DisplayName("Arma el request con los ids de la ruta y del nodo")
    void toRequestShouldBuildRequestWithRutaAndNodoIds() {
        ListarMicroleccionesRequest request = mapper.toRequest(RUTA_ID, NODO_ID);

        assertThat(request.rutaId()).isEqualTo(RUTA_ID);
        assertThat(request.nodoId()).isEqualTo(NODO_ID);
    }

    @Test
    @DisplayName("Mapea cada campo del item a la respuesta")
    void toResponseShouldMapEveryItemField() {
        ListarMicroleccionesResponse input = response(item(REVISADO_EN));

        List<MicroleccionResponse> result = toResponse(input);

        thenResponseHasItemFields(result.getFirst());
    }

    @Test
    @DisplayName("Mapea cada campo de la fuente a la respuesta de fuente")
    void toResponseShouldMapEveryFuenteField() {
        ListarMicroleccionesResponse input = response(item(REVISADO_EN));

        List<MicroleccionResponse> result = toResponse(input);

        thenFuenteResponseHasFuenteFields(result.getFirst().fuentes());
    }

    @Test
    @DisplayName("Conserva la fecha de revisión nula")
    void toResponseShouldKeepNullRevisadoEn() {
        ListarMicroleccionesResponse input = response(item(null));

        List<MicroleccionResponse> result = toResponse(input);

        assertThat(result.getFirst().revisadoEn()).isNull();
    }

    @Test
    @DisplayName("Devuelve lista vacía cuando no hay items")
    void toResponseShouldReturnEmptyListWhenNoItems() {
        ListarMicroleccionesResponse input = response();

        List<MicroleccionResponse> result = toResponse(input);

        assertThat(result).isEmpty();
    }

    // --- act ---
    private List<MicroleccionResponse> toResponse(ListarMicroleccionesResponse input) {
        return mapper.toResponse(input);
    }

    // --- assert ---
    private void thenResponseHasItemFields(MicroleccionResponse response) {
        assertThat(response.id()).isEqualTo(MICROLECCION_ID);
        assertThat(response.rutaId()).isEqualTo(RUTA_ID);
        assertThat(response.nodoId()).isEqualTo(NODO_ID);
        assertThat(response.titulo()).isEqualTo(TITULO);
        assertThat(response.nivel()).isEqualTo(NIVEL);
        assertThat(response.contenido()).isEqualTo(CONTENIDO);
        assertThat(response.origen()).isEqualTo(ORIGEN);
        assertThat(response.revisadoEn()).isEqualTo(REVISADO_EN);
    }

    private void thenFuenteResponseHasFuenteFields(List<FuenteMicroleccionResponse> fuentes) {
        assertThat(fuentes).hasSize(1);
        FuenteMicroleccionResponse fuente = fuentes.getFirst();
        assertThat(fuente.id()).isEqualTo(FUENTE_ID);
        assertThat(fuente.titulo()).isEqualTo(FUENTE_TITULO);
        assertThat(fuente.url()).isEqualTo(FUENTE_URL);
        assertThat(fuente.licencia()).isEqualTo(FUENTE_LICENCIA);
        assertThat(fuente.uso()).isEqualTo(FUENTE_USO);
        assertThat(fuente.permiteUsoComercial()).isTrue();
        assertThat(fuente.ubicacion()).isEqualTo(FUENTE_UBICACION);
    }

    // --- helpers ---
    private ListarMicroleccionesResponse response(MicroleccionItem... items) {
        return new ListarMicroleccionesResponse(List.of(items));
    }

    private MicroleccionItem item(OffsetDateTime revisadoEn) {
        return new MicroleccionItem(
                MICROLECCION_ID,
                RUTA_ID,
                NODO_ID,
                TITULO,
                NIVEL,
                CONTENIDO,
                ORIGEN,
                revisadoEn,
                List.of(fuenteItem()));
    }

    private FuenteMicroleccionItem fuenteItem() {
        return new FuenteMicroleccionItem(
                FUENTE_ID,
                FUENTE_TITULO,
                FUENTE_URL,
                FUENTE_LICENCIA,
                FUENTE_USO,
                true,
                FUENTE_UBICACION);
    }
}
