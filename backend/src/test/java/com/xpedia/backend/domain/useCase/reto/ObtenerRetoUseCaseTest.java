package com.xpedia.backend.domain.useCase.reto;

import com.xpedia.backend.domain.dto.reto.CriterioRubricaItem;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoRequest;
import com.xpedia.backend.domain.dto.reto.ObtenerRetoResponse;
import com.xpedia.backend.domain.dto.reto.RubricaItem;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.reto.ObtenerRetoMapper;
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
class ObtenerRetoUseCaseTest {

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

    private ObtenerRetoUseCase obtenerRetoUseCase;

    @BeforeEach
    void setUp() {
        obtenerRetoUseCase = new ObtenerRetoUseCase(retoService, new ObtenerRetoMapper());
    }

    @Test
    @DisplayName("Delega al servicio la ruta, el nodo y el reto del request")
    void executeShouldDelegateIdsToService() {
        givenServiceReturnsReto();

        obtener();

        thenServiceObtainedReto();
    }

    @Test
    @DisplayName("Devuelve el reto mapeado con su rúbrica")
    void executeShouldReturnMappedReto() {
        givenServiceReturnsReto();

        ObtenerRetoResponse response = obtener();

        thenResponseHasMappedReto(response);
    }

    @Test
    @DisplayName("Propaga el error del servicio cuando el reto no existe")
    void executeShouldPropagateErrorWhenServiceFails() {
        ResourceNotFoundException error = new ResourceNotFoundException("reto", "id", RETO_ID);
        givenServiceFails(error);

        assertThatThrownBy(this::obtener).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceReturnsReto() {
        when(retoService.obtener(RUTA_ID, NODO_ID, RETO_ID)).thenReturn(reto());
    }

    private void givenServiceFails(ResourceNotFoundException error) {
        when(retoService.obtener(RUTA_ID, NODO_ID, RETO_ID)).thenThrow(error);
    }

    // --- helpers ---
    private Reto reto() {
        return Reto.builder()
                .id(RETO_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .hitoId(HITO_ID)
                .tipo(TipoReto.RETO_PROYECTO)
                .titulo("Reto 02")
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
    private ObtenerRetoResponse obtener() {
        return obtenerRetoUseCase.execute(new ObtenerRetoRequest(RUTA_ID, NODO_ID, RETO_ID));
    }

    // --- assert ---
    private void thenServiceObtainedReto() {
        verify(retoService).obtener(RUTA_ID, NODO_ID, RETO_ID);
    }

    private void thenResponseHasMappedReto(ObtenerRetoResponse response) {
        assertThat(response.id()).isEqualTo(RETO_ID);
        assertThat(response.rutaId()).isEqualTo(RUTA_ID);
        assertThat(response.nodoId()).isEqualTo(NODO_ID);
        assertThat(response.hitoId()).isEqualTo(HITO_ID);
        assertThat(response.tipo()).isEqualTo(TipoReto.RETO_PROYECTO);
        assertThat(response.titulo()).isEqualTo("Reto 02");
        assertThat(response.nivel()).isEqualTo((short) 2);
        assertThat(response.contenido()).isEqualTo(CONTENIDO);
        assertThat(response.origen()).isEqualTo("HUMANO");
        assertThat(response.revisadoEn()).isEqualTo(REVISADO_EN);
        thenRubricaIsMapped(response.rubrica());
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
