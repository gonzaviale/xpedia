package com.xpedia.backend.domain.model.intento;

import com.xpedia.backend.domain.model.cuestionario.Pregunta;
import com.xpedia.backend.domain.model.enums.Confianza;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Respuesta {

    private UUID preguntaId;

    private Short elegida;

    private Boolean correcta;

    private Confianza confianza;

    private Integer milisegundos;

    // Se completan al corregir y no se persisten.
    private Short opcionCorrecta;

    private String explicacion;

    public Respuesta corregir(Pregunta pregunta) {
        return toBuilder()
                .correcta(pregunta.esCorrecta(elegida))
                .opcionCorrecta(pregunta.getCorrecta())
                .explicacion(pregunta.getExplicacion())
                .build();
    }
}
