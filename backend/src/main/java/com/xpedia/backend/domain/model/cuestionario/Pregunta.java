package com.xpedia.backend.domain.model.cuestionario;

import com.xpedia.backend.domain.model.enums.TipoPregunta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pregunta {

    private UUID id;

    private Short posicion;

    private TipoPregunta tipo;

    private String enunciado;

    private List<String> opciones;

    private Short correcta;

    private String explicacion;

    public boolean admiteOpcion(Short elegida) {
        return elegida != null && elegida >= 0 && elegida < opciones.size();
    }

    public boolean esCorrecta(Short elegida) {
        return correcta.equals(elegida);
    }
}
