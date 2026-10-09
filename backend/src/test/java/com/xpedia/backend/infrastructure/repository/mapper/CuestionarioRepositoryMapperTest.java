package com.xpedia.backend.infrastructure.repository.mapper;

import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.model.cuestionario.Pregunta;
import com.xpedia.backend.domain.model.enums.TipoPregunta;
import com.xpedia.backend.infrastructure.repository.entity.ActividadEntity;
import com.xpedia.backend.infrastructure.repository.entity.PreguntaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CuestionarioRepositoryMapperTest {

    private static final UUID ACTIVIDAD_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final String TITULO = "Escucha activa";
    private static final Short NIVEL = 2;
    private static final Short POSICION = 1;
    private static final String ENUNCIADO = "¿Qué es parafrasear?";
    private static final List<String> OPCIONES = List.of("Repetir igual", "Decirlo con otras palabras");
    private static final Short CORRECTA = 1;
    private static final String EXPLICACION = "Porque es decirlo con otras palabras";

    private CuestionarioRepositoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CuestionarioRepositoryMapper();
    }

    @Test
    @DisplayName("Mapea todos los campos de la actividad y de sus preguntas")
    void toDomainShouldMapAllFieldsOfActividadAndPreguntas() {
        Cuestionario cuestionario = mapper.toDomain(actividad(), List.of(pregunta("APLICACION")));

        thenCuestionarioHasAllFields(cuestionario);
    }

    @Test
    @DisplayName("Devuelve un cuestionario sin preguntas cuando no hay entidades de pregunta")
    void toDomainShouldReturnCuestionarioWithoutPreguntasWhenThereAreNone() {
        Cuestionario cuestionario = mapper.toDomain(actividad(), List.of());

        assertThat(cuestionario.getPreguntas()).isEmpty();
    }

    @Test
    @DisplayName("Convierte el tipo de pregunta al enum de dominio")
    void toDomainShouldConvertTipoToEnum() {
        Cuestionario cuestionario = mapper.toDomain(actividad(), List.of(pregunta("DETALLE")));

        assertThat(cuestionario.getPreguntas().getFirst().getTipo()).isEqualTo(TipoPregunta.DETALLE);
    }

    // --- helpers ---
    private ActividadEntity actividad() {
        return ActividadEntity.builder()
                .id(ACTIVIDAD_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .titulo(TITULO)
                .nivel(NIVEL)
                .build();
    }

    private PreguntaEntity pregunta(String tipo) {
        return PreguntaEntity.builder()
                .id(PREGUNTA_ID)
                .actividadId(ACTIVIDAD_ID)
                .posicion(POSICION)
                .tipo(tipo)
                .enunciado(ENUNCIADO)
                .opciones(OPCIONES)
                .correcta(CORRECTA)
                .explicacion(EXPLICACION)
                .build();
    }

    // --- assert ---
    private void thenCuestionarioHasAllFields(Cuestionario cuestionario) {
        assertThat(cuestionario.getId()).isEqualTo(ACTIVIDAD_ID);
        assertThat(cuestionario.getRutaId()).isEqualTo(RUTA_ID);
        assertThat(cuestionario.getNodoId()).isEqualTo(NODO_ID);
        assertThat(cuestionario.getTitulo()).isEqualTo(TITULO);
        assertThat(cuestionario.getNivel()).isEqualTo(NIVEL);
        Pregunta pregunta = cuestionario.getPreguntas().getFirst();
        assertThat(pregunta.getId()).isEqualTo(PREGUNTA_ID);
        assertThat(pregunta.getPosicion()).isEqualTo(POSICION);
        assertThat(pregunta.getTipo()).isEqualTo(TipoPregunta.APLICACION);
        assertThat(pregunta.getEnunciado()).isEqualTo(ENUNCIADO);
        assertThat(pregunta.getOpciones()).containsExactlyElementsOf(OPCIONES);
        assertThat(pregunta.getCorrecta()).isEqualTo(CORRECTA);
        assertThat(pregunta.getExplicacion()).isEqualTo(EXPLICACION);
    }
}
