package com.xpedia.backend.domain.model.reto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RetoTest {

    private static final String CONSIGNA = "Respondé ñ";
    private static final String CONTEXTO = "Caso público";
    private static final String FORMATO_ENTREGA = "Texto";

    @Test
    @DisplayName("Conserva las tres claves públicas cuando son texto")
    void filtrarContenidoPublicoShouldKeepAllowedKeys() {
        Map<String, Object> contenido = Map.of(
                "consigna", CONSIGNA,
                "contexto", CONTEXTO,
                "formatoEntrega", FORMATO_ENTREGA);

        Map<String, Object> publico = filtrar(contenido);

        assertThat(publico).containsExactlyInAnyOrderEntriesOf(contenido);
    }

    @Test
    @DisplayName("Descarta las claves que no están en la lista pública")
    void filtrarContenidoPublicoShouldDropExtraKeys() {
        Map<String, Object> contenido = Map.of(
                "consigna", CONSIGNA,
                "respuestaEsperada", "SECRETO",
                "evaluador", Map.of("clave", "SECRETO"));

        Map<String, Object> publico = filtrar(contenido);

        assertThat(publico).containsOnlyKeys("consigna");
    }

    @Test
    @DisplayName("Descarta las claves públicas cuyo valor no es texto")
    void filtrarContenidoPublicoShouldDropNonStringValues() {
        Map<String, Object> contenido = Map.of(
                "consigna", CONSIGNA,
                "contexto", Map.of("oculto", "SECRETO"),
                "formatoEntrega", 123);

        Map<String, Object> publico = filtrar(contenido);

        assertThat(publico).containsOnlyKeys("consigna");
    }

    @Test
    @DisplayName("Omite las claves públicas ausentes")
    void filtrarContenidoPublicoShouldOmitMissingKeys() {
        Map<String, Object> contenido = Map.of("consigna", CONSIGNA);

        Map<String, Object> publico = filtrar(contenido);

        assertThat(publico).containsOnlyKeys("consigna");
    }

    @Test
    @DisplayName("Devuelve un mapa vacío cuando el contenido está vacío")
    void filtrarContenidoPublicoShouldReturnEmptyMapWhenContenidoIsEmpty() {
        Map<String, Object> publico = filtrar(Map.of());

        assertThat(publico).isEmpty();
    }

    @Test
    @DisplayName("Devuelve un mapa inmutable")
    void filtrarContenidoPublicoShouldReturnUnmodifiableMap() {
        Map<String, Object> publico = filtrar(Map.of("consigna", CONSIGNA));

        assertThatThrownBy(() -> publico.put("contexto", CONTEXTO)).isInstanceOf(UnsupportedOperationException.class);
    }

    // --- act ---
    private Map<String, Object> filtrar(Map<String, Object> contenido) {
        return Reto.filtrarContenidoPublico(contenido);
    }
}
