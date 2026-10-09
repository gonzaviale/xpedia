package com.xpedia.backend.infrastructure.presentation.mapper.cuestionario;

import com.xpedia.backend.domain.dto.cuestionario.CuestionarioItem;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosRequest;
import com.xpedia.backend.domain.dto.cuestionario.ListarCuestionariosResponse;
import com.xpedia.backend.domain.dto.cuestionario.PreguntaItem;
import com.xpedia.backend.domain.model.enums.TipoPregunta;
import com.xpedia.backend.infrastructure.presentation.dto.cuestionario.CuestionarioResponse;
import com.xpedia.backend.infrastructure.presentation.dto.cuestionario.PreguntaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CuestionarioPresentationMapperTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID CUESTIONARIO_ID = UUID.randomUUID();
    private static final UUID PREGUNTA_ID = UUID.randomUUID();
    private static final String TITULO = "Escucha activa";
    private static final Short NIVEL = 2;
    private static final Short POSICION = 1;
    private static final String ENUNCIADO = "¿Qué es parafrasear?";
    private static final List<String> OPCIONES = List.of("Repetir igual", "Decirlo con otras palabras");

    private CuestionarioPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CuestionarioPresentationMapper();
    }

    @Test
    @DisplayName("Arma el pedido de dominio con el id de la ruta y del nodo")
    void toListarRequestShouldBuildRequestWithRutaIdAndNodoId() {
        ListarCuestionariosRequest request = mapper.toListarRequest(RUTA_ID, NODO_ID);

        assertThat(request.rutaId()).isEqualTo(RUTA_ID);
        assertThat(request.nodoId()).isEqualTo(NODO_ID);
    }

    @Test
    @DisplayName("Mapea todos los campos del cuestionario y de sus preguntas a la respuesta HTTP")
    void toResponseShouldMapAllFieldsOfCuestionarioAndPreguntas() {
        ListarCuestionariosResponse response = new ListarCuestionariosResponse(List.of(cuestionarioItem()));

        List<CuestionarioResponse> result = mapper.toResponse(response);

        CuestionarioResponse cuestionario = result.getFirst();
        assertThat(cuestionario.id()).isEqualTo(CUESTIONARIO_ID);
        assertThat(cuestionario.rutaId()).isEqualTo(RUTA_ID);
        assertThat(cuestionario.nodoId()).isEqualTo(NODO_ID);
        assertThat(cuestionario.titulo()).isEqualTo(TITULO);
        assertThat(cuestionario.nivel()).isEqualTo(NIVEL);
        PreguntaResponse pregunta = cuestionario.preguntas().getFirst();
        assertThat(pregunta.id()).isEqualTo(PREGUNTA_ID);
        assertThat(pregunta.posicion()).isEqualTo(POSICION);
        assertThat(pregunta.tipo()).isEqualTo(TipoPregunta.CONCEPTO);
        assertThat(pregunta.enunciado()).isEqualTo(ENUNCIADO);
        assertThat(pregunta.opciones()).containsExactlyElementsOf(OPCIONES);
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando no hay cuestionarios")
    void toResponseShouldReturnEmptyListWhenThereAreNoCuestionarios() {
        List<CuestionarioResponse> result = mapper.toResponse(new ListarCuestionariosResponse(List.of()));

        assertThat(result).isEmpty();
    }

    // --- helpers ---
    private CuestionarioItem cuestionarioItem() {
        PreguntaItem pregunta = new PreguntaItem(PREGUNTA_ID, POSICION, TipoPregunta.CONCEPTO, ENUNCIADO, OPCIONES);
        return new CuestionarioItem(CUESTIONARIO_ID, RUTA_ID, NODO_ID, TITULO, NIVEL, List.of(pregunta));
    }
}
