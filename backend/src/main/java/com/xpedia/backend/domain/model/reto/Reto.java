package com.xpedia.backend.domain.model.reto;

import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Reto {
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
}
