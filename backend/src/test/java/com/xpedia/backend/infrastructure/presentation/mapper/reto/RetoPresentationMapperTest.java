package com.xpedia.backend.infrastructure.presentation.mapper.reto;

import com.xpedia.backend.domain.dto.reto.CriterioRubricaItem;
import com.xpedia.backend.domain.dto.reto.ListarRetosRequest;
import com.xpedia.backend.domain.dto.reto.ListarRetosResponse;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoRequest;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoResponse;
import com.xpedia.backend.domain.dto.reto.RetoItem;
import com.xpedia.backend.domain.dto.reto.RubricaItem;
import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.infrastructure.presentation.dto.reto.CriterioRubricaResponse;
import com.xpedia.backend.infrastructure.presentation.dto.reto.RetoResponse;
import com.xpedia.backend.infrastructure.presentation.dto.reto.RubricaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RetoPresentationMapperTest {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID RETO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000001");
    private static final UUID HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-08T10:00:00-03:00");
    private static final Map<String, Object> CONTENIDO = Map.of("consigna", "Responder ñ");

    private RetoPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RetoPresentationMapper();
    }

    @Test
    @DisplayName("Arma el request de listar con la ruta y el nodo")
    void toListarRequestShouldCarryRutaAndNodoIds() {
        ListarRetosRequest request = mapper.toListarRequest(RUTA_ID, NODO_ID);

        assertThat(request.rutaId()).isEqualTo(RUTA_ID);
        assertThat(request.nodoId()).isEqualTo(NODO_ID);
    }

    @Test
    @DisplayName("Arma el request de obtener con la ruta, el nodo y el reto")
    void toObtenerRequestShouldCarryRutaNodoAndRetoIds() {
        ObtenerRetoRequest request = mapper.toObtenerRequest(RUTA_ID, NODO_ID, RETO_ID);

        assertThat(request.rutaId()).isEqualTo(RUTA_ID);
        assertThat(request.nodoId()).isEqualTo(NODO_ID);
        assertThat(request.retoId()).isEqualTo(RETO_ID);
    }

    @Test
    @DisplayName("Mapea cada reto de la lista a su respuesta con campos y rúbrica")
    void toResponseShouldMapEveryRetoOfListarResponse() {
        ListarRetosResponse response = new ListarRetosResponse(List.of(retoItem()));

        List<RetoResponse> result = mapper.toResponse(response);

        assertThat(result).hasSize(1);
        thenResponseHasRetoFields(result.getFirst());
        thenRubricaHasFields(result.getFirst().rubrica());
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando la respuesta de listar no tiene retos")
    void toResponseShouldReturnEmptyListWhenListarResponseIsEmpty() {
        List<RetoResponse> result = mapper.toResponse(new ListarRetosResponse(List.of()));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Mapea la respuesta de obtener a la respuesta del reto con campos y rúbrica")
    void toResponseShouldMapObtenerResponse() {
        RetoResponse result = mapper.toResponse(obtenerResponse());

        thenResponseHasRetoFields(result);
        thenRubricaHasFields(result.rubrica());
    }

    // --- helpers ---
    private RetoItem retoItem() {
        return new RetoItem(RETO_ID, RUTA_ID, NODO_ID, HITO_ID, TipoReto.RETO_PROYECTO, "Reto 01", (short) 2,
                CONTENIDO, "HUMANO", REVISADO_EN, rubricaItem());
    }

    private ObtenerRetoResponse obtenerResponse() {
        return new ObtenerRetoResponse(RETO_ID, RUTA_ID, NODO_ID, HITO_ID, TipoReto.RETO_PROYECTO, "Reto 01",
                (short) 2, CONTENIDO, "HUMANO", REVISADO_EN, rubricaItem());
    }

    private RubricaItem rubricaItem() {
        CriterioRubricaItem criterio = new CriterioRubricaItem(
                CRITERIO_ID, (short) 1, "Política", null, (short) 3, new BigDecimal("0.75"), true);
        return new RubricaItem(
                RUBRICA_ID, "Escritura", null, new BigDecimal("1.00"), new BigDecimal("2.25"), List.of(criterio));
    }

    // --- assert ---
    private void thenResponseHasRetoFields(RetoResponse response) {
        assertThat(response.id()).isEqualTo(RETO_ID);
        assertThat(response.rutaId()).isEqualTo(RUTA_ID);
        assertThat(response.nodoId()).isEqualTo(NODO_ID);
        assertThat(response.hitoId()).isEqualTo(HITO_ID);
        assertThat(response.tipo()).isEqualTo(TipoReto.RETO_PROYECTO);
        assertThat(response.titulo()).isEqualTo("Reto 01");
        assertThat(response.nivel()).isEqualTo((short) 2);
        assertThat(response.contenido()).isEqualTo(CONTENIDO);
        assertThat(response.origen()).isEqualTo("HUMANO");
        assertThat(response.revisadoEn()).isEqualTo(REVISADO_EN);
    }

    private void thenRubricaHasFields(RubricaResponse rubrica) {
        assertThat(rubrica.id()).isEqualTo(RUBRICA_ID);
        assertThat(rubrica.nombre()).isEqualTo("Escritura");
        assertThat(rubrica.descripcion()).isNull();
        assertThat(rubrica.puntajeAprobacion()).isEqualByComparingTo("1.00");
        assertThat(rubrica.puntajeMaximo()).isEqualByComparingTo("2.25");
        assertThat(rubrica.criterios()).hasSize(1);
        CriterioRubricaResponse criterio = rubrica.criterios().getFirst();
        assertThat(criterio.id()).isEqualTo(CRITERIO_ID);
        assertThat(criterio.posicion()).isEqualTo((short) 1);
        assertThat(criterio.nombre()).isEqualTo("Política");
        assertThat(criterio.descripcion()).isNull();
        assertThat(criterio.puntajeMax()).isEqualTo((short) 3);
        assertThat(criterio.peso()).isEqualByComparingTo("0.75");
        assertThat(criterio.eliminatorio()).isTrue();
    }
}
