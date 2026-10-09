package com.xpedia.backend.domain.mapper.cuestionario;

import com.xpedia.backend.domain.dto.cuestionario.CuestionarioItem;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosResponse;
import com.xpedia.backend.domain.dto.cuestionario.PreguntaItem;
import com.xpedia.backend.domain.model.cuestionario.Cuestionario;
import com.xpedia.backend.domain.model.cuestionario.Pregunta;
import com.xpedia.backend.domain.model.enums.TipoPregunta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListarCuestionariosMapperTest {

    private static final UUID CUESTIONARIO_ID = UUID.randomUUID();
    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final String TITULO = "Escucha activa";
    private static final Short NIVEL = 2;
    private static final Short POSICION = 1;
    private static final String ENUNCIADO = "¿Qué es parafrasear?";
    private static final List<String> OPCIONES = List.of("Repetir igual", "Decirlo con otras palabras");

    private ListarCuestionariosMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ListarCuestionariosMapper();
    }

    @Test
    @DisplayName("Mapea todos los campos del cuestionario y de sus preguntas")
    void toResponseShouldMapAllFieldsOfCuestionarioAndPreguntas() {
        ListarCuestionariosResponse response = mapper.toResponse(List.of(cuestionario()));

        thenItemHasAllFields(response.content().getFirst());
    }

    @Test
    @DisplayName("No expone la opción correcta ni la explicación")
    void toResponseShouldNotExposeCorrectOptionNorExplanation() {
        ListarCuestionariosResponse response = mapper.toResponse(List.of(cuestionario()));

        assertThat(PreguntaItem.class.getRecordComponents())
                .extracting(componente -> componente.getName())
                .doesNotContain("correcta", "explicacion");
        assertThat(response.content().getFirst().preguntas()).hasSize(1);
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando no hay cuestionarios")
    void toResponseShouldReturnEmptyContentWhenThereAreNoCuestionarios() {
        ListarCuestionariosResponse response = mapper.toResponse(List.of());

        assertThat(response.content()).isEmpty();
    }

    // --- helpers ---
    private Cuestionario cuestionario() {
        Pregunta pregunta = Pregunta.builder()
                .id(PREGUNTA_ID)
                .posicion(POSICION)
                .tipo(TipoPregunta.CONCEPTO)
                .enunciado(ENUNCIADO)
                .opciones(OPCIONES)
                .correcta((short) 1)
                .explicacion("Porque es decirlo con otras palabras")
                .build();
        return Cuestionario.builder()
                .id(CUESTIONARIO_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .titulo(TITULO)
                .nivel(NIVEL)
                .preguntas(List.of(pregunta))
                .build();
    }

    // --- assert ---
    private void thenItemHasAllFields(CuestionarioItem item) {
        assertThat(item.id()).isEqualTo(CUESTIONARIO_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.nodoId()).isEqualTo(NODO_ID);
        assertThat(item.titulo()).isEqualTo(TITULO);
        assertThat(item.nivel()).isEqualTo(NIVEL);
        PreguntaItem pregunta = item.preguntas().getFirst();
        assertThat(pregunta.id()).isEqualTo(PREGUNTA_ID);
        assertThat(pregunta.posicion()).isEqualTo(POSICION);
        assertThat(pregunta.tipo()).isEqualTo(TipoPregunta.CONCEPTO);
        assertThat(pregunta.enunciado()).isEqualTo(ENUNCIADO);
        assertThat(pregunta.opciones()).containsExactlyElementsOf(OPCIONES);
    }
}
