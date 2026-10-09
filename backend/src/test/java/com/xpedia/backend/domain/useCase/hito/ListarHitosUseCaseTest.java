package com.xpedia.backend.domain.useCase.hito;

import com.xpedia.backend.domain.dto.hito.HitoItem;
import com.xpedia.backend.domain.dto.hito.ListarHitosRequest;
import com.xpedia.backend.domain.dto.hito.ListarHitosResponse;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.hito.ListarHitosMapper;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.service.hito.HitoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarHitosUseCaseTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID HITO_ID = UUID.randomUUID();
    private static final Short POSICION = 1;
    private static final String TITULO = "Primer hito";
    private static final String OBJETIVO = "Aprender lo básico";
    private static final BigDecimal HORAS_ESTIMADAS = new BigDecimal("12.5");
    private static final String EVIDENCIA_ESPERADA = "Un informe";

    @Mock
    private HitoService hitoService;

    private ListarHitosUseCase listarHitosUseCase;

    @BeforeEach
    void setUp() {
        listarHitosUseCase = new ListarHitosUseCase(hitoService, new ListarHitosMapper());
    }

    @Test
    @DisplayName("Lista los hitos de la ruta pedida y los devuelve mapeados")
    void executeShouldReturnMappedHitosOfRuta() {
        givenServiceListsHitos(List.of(hito()));

        ListarHitosResponse response = listar();

        thenResponseHasMappedHito(response);
    }

    @Test
    @DisplayName("Delega la consulta al servicio con el id de la ruta")
    void executeShouldDelegateToServiceWithRutaId() {
        givenServiceListsHitos(List.of());

        listar();

        thenServiceListedHitosOfRuta();
    }

    @Test
    @DisplayName("Propaga el error de dominio del servicio sin convertirlo")
    void executeShouldPropagateDomainErrorWhenServiceFails() {
        ResourceNotFoundException error = new ResourceNotFoundException("ruta", "id", RUTA_ID);
        givenServiceFails(error);

        assertThatThrownBy(this::listar).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceListsHitos(List<Hito> hitos) {
        when(hitoService.listar(RUTA_ID)).thenReturn(hitos);
    }

    private void givenServiceFails(ResourceNotFoundException error) {
        when(hitoService.listar(RUTA_ID)).thenThrow(error);
    }

    // --- helpers ---
    private Hito hito() {
        return Hito.builder()
                .id(HITO_ID)
                .rutaId(RUTA_ID)
                .posicion(POSICION)
                .titulo(TITULO)
                .objetivo(OBJETIVO)
                .horasEstimadas(HORAS_ESTIMADAS)
                .esFinal(true)
                .evidenciaEsperada(EVIDENCIA_ESPERADA)
                .build();
    }

    // --- act ---
    private ListarHitosResponse listar() {
        return listarHitosUseCase.execute(new ListarHitosRequest(RUTA_ID));
    }

    // --- assert ---
    private void thenServiceListedHitosOfRuta() {
        verify(hitoService).listar(RUTA_ID);
    }

    private void thenResponseHasMappedHito(ListarHitosResponse response) {
        assertThat(response.content()).hasSize(1);
        HitoItem item = response.content().getFirst();
        assertThat(item.id()).isEqualTo(HITO_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.posicion()).isEqualTo(POSICION);
        assertThat(item.titulo()).isEqualTo(TITULO);
        assertThat(item.objetivo()).isEqualTo(OBJETIVO);
        assertThat(item.horasEstimadas()).isEqualTo(HORAS_ESTIMADAS);
        assertThat(item.esFinal()).isTrue();
        assertThat(item.evidenciaEsperada()).isEqualTo(EVIDENCIA_ESPERADA);
    }
}
