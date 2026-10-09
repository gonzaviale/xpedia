package com.xpedia.backend.domain.mapper.microleccion;

import com.xpedia.backend.domain.dto.microleccion.FuenteMicroleccionItem;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesResponse;
import com.xpedia.backend.domain.dto.microleccion.MicroleccionItem;
import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListarMicroleccionesMapperTest {

    private static final UUID MICROLECCION_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID FUENTE_ID = UUID.randomUUID();
    private static final String TITULO = "Lección inicial";
    private static final Short NIVEL = 3;
    private static final Map<String, Object> CONTENIDO = Map.of("texto", "Comunicación ñ", "pasos", List.of("saludo", "acción"));
    private static final String ORIGEN = "IA";
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-01T10:00:00Z");
    private static final String FUENTE_TITULO = "Fuente global";
    private static final String FUENTE_URL = "https://example.org/material";
    private static final String FUENTE_LICENCIA = "Licencia de prueba";
    private static final String FUENTE_USO = "ADAPTABLE";
    private static final String FUENTE_UBICACION = "Sección 1";

    private ListarMicroleccionesMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ListarMicroleccionesMapper();
    }

    @Test
    @DisplayName("Mapea cada campo de la microlección al item")
    void toResponseShouldMapEveryMicroleccionField() {
        Microleccion microleccion = microleccion(REVISADO_EN, List.of(fuente()));

        ListarMicroleccionesResponse response = toResponse(microleccion);

        thenItemHasMicroleccionFields(response.content().getFirst());
    }

    @Test
    @DisplayName("Mapea cada campo de la fuente al item de fuente")
    void toResponseShouldMapEveryFuenteField() {
        Microleccion microleccion = microleccion(REVISADO_EN, List.of(fuente()));

        ListarMicroleccionesResponse response = toResponse(microleccion);

        thenFuenteItemHasFuenteFields(response.content().getFirst().fuentes());
    }

    @Test
    @DisplayName("Conserva la fecha de revisión nula")
    void toResponseShouldKeepNullRevisadoEn() {
        Microleccion microleccion = microleccion(null, List.of(fuente()));

        ListarMicroleccionesResponse response = toResponse(microleccion);

        assertThat(response.content().getFirst().revisadoEn()).isNull();
    }

    @Test
    @DisplayName("Conserva el orden de las microlecciones")
    void toResponseShouldKeepMicroleccionOrder() {
        Microleccion primera = microleccion(REVISADO_EN, List.of());
        Microleccion segunda = microleccion(null, List.of());
        segunda.setId(UUID.randomUUID());

        ListarMicroleccionesResponse response = toResponse(primera, segunda);

        assertThat(response.content()).extracting(MicroleccionItem::id)
                .containsExactly(MICROLECCION_ID, segunda.getId());
    }

    @Test
    @DisplayName("Devuelve contenido vacío cuando no hay microlecciones")
    void toResponseShouldReturnEmptyContentWhenNoMicrolecciones() {
        ListarMicroleccionesResponse response = toResponse();

        assertThat(response.content()).isEmpty();
    }

    // --- act ---
    private ListarMicroleccionesResponse toResponse(Microleccion... microlecciones) {
        return mapper.toResponse(List.of(microlecciones));
    }

    // --- assert ---
    private void thenItemHasMicroleccionFields(MicroleccionItem item) {
        assertThat(item.id()).isEqualTo(MICROLECCION_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.nodoId()).isEqualTo(NODO_ID);
        assertThat(item.titulo()).isEqualTo(TITULO);
        assertThat(item.nivel()).isEqualTo(NIVEL);
        assertThat(item.contenido()).isEqualTo(CONTENIDO);
        assertThat(item.origen()).isEqualTo(ORIGEN);
        assertThat(item.revisadoEn()).isEqualTo(REVISADO_EN);
    }

    private void thenFuenteItemHasFuenteFields(List<FuenteMicroleccionItem> fuentes) {
        assertThat(fuentes).hasSize(1);
        FuenteMicroleccionItem fuente = fuentes.getFirst();
        assertThat(fuente.id()).isEqualTo(FUENTE_ID);
        assertThat(fuente.titulo()).isEqualTo(FUENTE_TITULO);
        assertThat(fuente.url()).isEqualTo(FUENTE_URL);
        assertThat(fuente.licencia()).isEqualTo(FUENTE_LICENCIA);
        assertThat(fuente.uso()).isEqualTo(FUENTE_USO);
        assertThat(fuente.permiteUsoComercial()).isTrue();
        assertThat(fuente.ubicacion()).isEqualTo(FUENTE_UBICACION);
    }

    // --- helpers ---
    private Microleccion microleccion(OffsetDateTime revisadoEn, List<FuenteMicroleccion> fuentes) {
        return Microleccion.builder()
                .id(MICROLECCION_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .titulo(TITULO)
                .nivel(NIVEL)
                .contenido(CONTENIDO)
                .origen(ORIGEN)
                .revisadoEn(revisadoEn)
                .fuentes(fuentes)
                .build();
    }

    private FuenteMicroleccion fuente() {
        return new FuenteMicroleccion(
                FUENTE_ID,
                FUENTE_TITULO,
                FUENTE_URL,
                FUENTE_LICENCIA,
                FUENTE_USO,
                true,
                FUENTE_UBICACION);
    }
}
