package com.xpedia.backend.domain.model.cuestionario;

import com.xpedia.backend.domain.exception.BusinessRuleException;
import com.xpedia.backend.domain.model.intento.Respuesta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cuestionario {

    private UUID id;

    private UUID rutaId;

    private UUID nodoId;

    private String titulo;

    private Short nivel;

    private List<Pregunta> preguntas;

    public void validarRespuestas(List<Respuesta> respuestas) {
        Set<UUID> respondidas = new HashSet<>();
        for (Respuesta respuesta : respuestas) {
            Pregunta pregunta = buscarPregunta(respuesta.getPreguntaId());
            if (!respondidas.add(pregunta.getId())) {
                throw new BusinessRuleException("La pregunta " + pregunta.getId() + " está respondida más de una vez");
            }
            if (!pregunta.admiteOpcion(respuesta.getElegida())) {
                throw new BusinessRuleException("La opción " + respuesta.getElegida()
                        + " no existe en la pregunta " + pregunta.getId());
            }
        }
        if (respondidas.size() != preguntas.size()) {
            throw new BusinessRuleException("Hay que responder las " + preguntas.size()
                    + " preguntas del cuestionario");
        }
    }

    public Pregunta buscarPregunta(UUID preguntaId) {
        return preguntas.stream()
                .filter(pregunta -> pregunta.getId().equals(preguntaId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException(
                        "La pregunta " + preguntaId + " no pertenece al cuestionario"));
    }
}
