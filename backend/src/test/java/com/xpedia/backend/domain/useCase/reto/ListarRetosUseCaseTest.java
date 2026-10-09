package com.xpedia.backend.domain.useCase.reto;

import com.xpedia.backend.domain.dto.reto.CriterioRubricaItem;
import com.xpedia.backend.domain.dto.reto.ListarRetosRequest;
import com.xpedia.backend.domain.dto.reto.ListarRetosResponse;
import com.xpedia.backend.domain.dto.reto.RetoItem;
import com.xpedia.backend.domain.dto.reto.RubricaItem;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.reto.ListarRetosMapper;
import com.xpedia.backend.domain.model.enums.TipoReto;
import com.xpedia.backend.domain.model.reto.Reto;
import com.xpedia.backend.domain.model.rubrica.CriterioRubrica;
import com.xpedia.backend.domain.model.rubrica.Rubrica;
import com.xpedia.backend.domain.service.reto.RetoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarRetosUseCaseTest {

    private static final UUID RUTA_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");
    private static final UUID NODO_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");
    private static final UUID RETO_ID = UUID.fromString("d3000000-0000-4000-8000-000000000001");
    private static final UUID HITO_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID RUBRICA_ID = UUID.fromString("d1000000-0000-4000-8000-000000000001");
    private static final UUID CRITERIO_ID = UUID.fromString("d2000000-0000-4000-8000-000000000001");
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-08T10:00:00-03:00");
    private static final Map<String, Object> CONTENIDO = Map.of("consigna", "Responder");

    @Mock
    private RetoService retoService;

    private ListarRetosUseCase listarRetosUseCase;

    @BeforeEach
    void setUp() {
        listarRetosUseCase = new ListarRetosUseCase(retoService, new ListarRetosMapper());
    }

    @Test
    @DisplayName("Delega al servicio la ruta y el nodo del request")
    void executeShouldDelegateRutaAndNodoToService() {
        givenServiceReturnsRetos(List.of(reto()));

        listar();

        thenServiceListedRetosOfNodo();
    }

    @Test
    @DisplayName("Devuelve los retos mapeados con su rúbrica")
    void executeShouldReturnMappedRetos() {
        givenServiceReturnsRetos(List.of(reto()));

        ListarRetosResponse response = listar();

        thenResponseHasMappedReto(response);
    }

    @Test
    @DisplayName("Devuelve una lista vacía cuando el nodo no tiene retos")
    void executeShouldReturnEmptyContentWhenNodoHasNoRetos() {
        givenServiceReturnsRetos(List.of());

        ListarRetosResponse response = listar();

        assertThat(response.content()).isEmpty();
    }

    @Test
    @DisplayName("Propaga el error del servicio cuando el nodo no es visible")
    void executeShouldPropagateErrorWhenServiceFails() {
        ResourceNotFoundException error = new ResourceNotFoundException("nodo", "id", NODO_ID);
        givenServiceFails(error);

        assertThatThrownBy(this::listar).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceReturnsRetos(List<Reto> retos) {
        when(retoService.listar(RUTA_ID, NODO_ID)).thenReturn(retos);
    }

    private void givenServiceFails(ResourceNotFoundException error) {
        when(retoService.listar(RUTA_ID, NODO_ID)).thenThrow(error);
    }

    // --- helpers ---
    private Reto reto() {
        return Reto.builder()
                .id(RETO_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .hitoId(HITO_ID)
                .tipo(TipoReto.ENSAYO)
                .titulo("Reto 01")
                .nivel((short) 2)
                .contenido(CONTENIDO)
                .origen("HUMANO")
                .revisadoEn(REVISADO_EN)
                .rubrica(rubrica())
                .build();
    }

    private Rubrica rubrica() {
        CriterioRubrica criterio = new CriterioRubrica(
                CRITERIO_ID, (short) 1, "Claridad", "Detalle", (short) 3, new BigDecimal("0.50"), true);
        return new Rubrica(RUBRICA_ID, "Escritura", "Rúbrica base", new BigDecimal("1.00"), List.of(criterio));
    }

    // --- act ---
    private ListarRetosResponse listar() {
        return listarRetosUseCase.execute(new ListarRetosRequest(RUTA_ID, NODO_ID));
    }

    // --- assert ---
    private void thenServiceListedRetosOfNodo() {
        verify(retoService).listar(RUTA_ID, NODO_ID);
    }

    private void thenResponseHasMappedReto(ListarRetosResponse response) {
        assertThat(response.content()).hasSize(1);
        RetoItem item = response.content().getFirst();
        assertThat(item.id()).isEqualTo(RETO_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.nodoId()).isEqualTo(NODO_ID);
        assertThat(item.hitoId()).isEqualTo(HITO_ID);
        assertThat(item.tipo()).isEqualTo(TipoReto.ENSAYO);
        assertThat(item.titulo()).isEqualTo("Reto 01");
        assertThat(item.nivel()).isEqualTo((short) 2);
        assertThat(item.contenido()).isEqualTo(CONTENIDO);
        assertThat(item.origen()).isEqualTo("HUMANO");
        assertThat(item.revisadoEn()).isEqualTo(REVISADO_EN);
        thenRubricaIsMapped(item.rubrica());
    }

    private void thenRubricaIsMapped(RubricaItem rubrica) {
        assertThat(rubrica.id()).isEqualTo(RUBRICA_ID);
        assertThat(rubrica.nombre()).isEqualTo("Escritura");
        assertThat(rubrica.descripcion()).isEqualTo("Rúbrica base");
        assertThat(rubrica.puntajeAprobacion()).isEqualByComparingTo("1.00");
        assertThat(rubrica.puntajeMaximo()).isEqualByComparingTo("1.50");
        assertThat(rubrica.criterios()).hasSize(1);
        CriterioRubricaItem criterio = rubrica.criterios().getFirst();
        assertThat(criterio.id()).isEqualTo(CRITERIO_ID);
        assertThat(criterio.eliminatorio()).isTrue();
    }
}
