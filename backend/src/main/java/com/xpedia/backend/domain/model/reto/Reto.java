package com.xpedia.backend.domain.model.reto;

import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reto {

    /** Claves del contenido de la actividad que se exponen públicamente; cualquier otra se descarta. */
    private static final List<String> CLAVES_CONTENIDO_PUBLICO = List.of("consigna", "contexto", "formatoEntrega");

    private UUID id;

    private UUID rutaId;

    private UUID nodoId;

    private UUID hitoId;

    private TipoReto tipo;

    private String titulo;

    private Short nivel;

    private Map<String, Object> contenido;

    private String origen;

    private OffsetDateTime revisadoEn;

    private Rubrica rubrica;

    /** Deja solo las claves públicas del contenido cuyo valor es un texto; el resultado es inmutable. */
    public static Map<String, Object> filtrarContenidoPublico(Map<String, Object> contenido) {
        Map<String, Object> publico = new LinkedHashMap<>();
        for (String clave : CLAVES_CONTENIDO_PUBLICO) {
            Object valor = contenido.get(clave);
            if (valor instanceof String) {
                publico.put(clave, valor);
            }
        }
        return Map.copyOf(publico);
    }
}
