package com.xpedia.backend.domain.useCase.microleccion;

import com.xpedia.backend.domain.dto.microleccion.FuenteMicroleccionItem;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesRequest;
import com.xpedia.backend.domain.dto.microleccion.ListarMicroleccionesResponse;
import com.xpedia.backend.domain.dto.microleccion.MicroleccionItem;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.microleccion.ListarMicroleccionesMapper;
import com.xpedia.backend.domain.model.microleccion.FuenteMicroleccion;
import com.xpedia.backend.domain.model.microleccion.Microleccion;
import com.xpedia.backend.domain.service.microleccion.MicroleccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarMicroleccionesUseCaseTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID NODO_ID = UUID.randomUUID();
    private static final UUID MICROLECCION_ID = UUID.randomUUID();
    private static final UUID FUENTE_ID = UUID.randomUUID();
    private static final String TITULO = "Lección inicial";
    private static final Short NIVEL = 2;
    private static final Map<String, Object> CONTENIDO = Map.of("texto", "Hola");
    private static final String ORIGEN = "HUMANO";
    private static final OffsetDateTime REVISADO_EN = OffsetDateTime.parse("2026-10-01T10:00:00Z");
    private static final String FUENTE_TITULO = "Fuente global";
    private static final String FUENTE_URL = "https://example.org/material";
    private static final String FUENTE_LICENCIA = "Licencia de prueba";
    private static final String FUENTE_USO = "ADAPTABLE";
    private static final String FUENTE_UBICACION = "Sección 1";

    @Mock
    private MicroleccionService microleccionService;

    private ListarMicroleccionesUseCase listarMicroleccionesUseCase;

    @BeforeEach
    void setUp() {
        listarMicroleccionesUseCase = new ListarMicroleccionesUseCase(microleccionService, new ListarMicroleccionesMapper());
    }

    @Test
    @DisplayName("Lista las microlecciones del nodo delegando en el servicio con los ids de la ruta y del nodo")
    void executeShouldDelegateToServiceWithRutaAndNodoIds() {
        givenServiceReturnsMicroleccion();

        listar();

        thenServiceListedByRutaAndNodo();
    }

    @Test
    @DisplayName("Devuelve la microlección con todos sus campos y sus fuentes")
    void executeShouldReturnMicroleccionWithAllFieldsAndFuentes() {
        givenServiceReturnsMicroleccion();

        ListarMicroleccionesResponse response = listar();

        thenResponseHasMicroleccion(response);
    }

    @Test
    @DisplayName("Propaga el error del servicio sin transformarlo en una respuesta vacía")
    void executeShouldPropagateServiceError() {
        ResourceNotFoundException error = givenServiceThrowsNotFound();

        assertThatThrownBy(this::listar).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceReturnsMicroleccion() {
        when(microleccionService.listar(RUTA_ID, NODO_ID)).thenReturn(List.of(microleccion()));
    }

    private ResourceNotFoundException givenServiceThrowsNotFound() {
        ResourceNotFoundException error = new ResourceNotFoundException("nodo", "id", NODO_ID);
        when(microleccionService.listar(RUTA_ID, NODO_ID)).thenThrow(error);
        return error;
    }

    // --- act ---
    private ListarMicroleccionesResponse listar() {
        return listarMicroleccionesUseCase.execute(new ListarMicroleccionesRequest(RUTA_ID, NODO_ID));
    }

    // --- assert ---
    private void thenServiceListedByRutaAndNodo() {
        verify(microleccionService).listar(RUTA_ID, NODO_ID);
    }

    private void thenResponseHasMicroleccion(ListarMicroleccionesResponse response) {
        assertThat(response.content()).hasSize(1);
        MicroleccionItem item = response.content().getFirst();
        assertThat(item.id()).isEqualTo(MICROLECCION_ID);
        assertThat(item.rutaId()).isEqualTo(RUTA_ID);
        assertThat(item.nodoId()).isEqualTo(NODO_ID);
        assertThat(item.titulo()).isEqualTo(TITULO);
        assertThat(item.nivel()).isEqualTo(NIVEL);
        assertThat(item.contenido()).isEqualTo(CONTENIDO);
        assertThat(item.origen()).isEqualTo(ORIGEN);
        assertThat(item.revisadoEn()).isEqualTo(REVISADO_EN);
        assertThat(item.fuentes()).hasSize(1);
        FuenteMicroleccionItem fuente = item.fuentes().getFirst();
        assertThat(fuente.id()).isEqualTo(FUENTE_ID);
        assertThat(fuente.titulo()).isEqualTo(FUENTE_TITULO);
        assertThat(fuente.url()).isEqualTo(FUENTE_URL);
        assertThat(fuente.licencia()).isEqualTo(FUENTE_LICENCIA);
        assertThat(fuente.uso()).isEqualTo(FUENTE_USO);
        assertThat(fuente.permiteUsoComercial()).isTrue();
        assertThat(fuente.ubicacion()).isEqualTo(FUENTE_UBICACION);
    }

    // --- helpers ---
    private Microleccion microleccion() {
        return Microleccion.builder()
                .id(MICROLECCION_ID)
                .rutaId(RUTA_ID)
                .nodoId(NODO_ID)
                .titulo(TITULO)
                .nivel(NIVEL)
                .contenido(CONTENIDO)
                .origen(ORIGEN)
                .revisadoEn(REVISADO_EN)
                .fuentes(List.of(fuente()))
                .build();
    }

    private FuenteMicroleccion fuente() {
        return new FuenteMicroleccion(
                FUENTE_ID,
                FUENTE_TITULO,
                FUENTE_URL,
                FUENTE_LICENCIA,
                FUENTE_USO,
                true,
                FUENTE_UBICACION);
    }
}
