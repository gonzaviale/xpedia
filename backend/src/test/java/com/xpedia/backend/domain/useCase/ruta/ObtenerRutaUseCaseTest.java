package com.xpedia.backend.domain.useCase.ruta;

import com.xpedia.backend.domain.dto.ruta.ObtenerRutaRequest;
import com.xpedia.backend.domain.dto.ruta.ObtenerRutaResponse;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.ruta.ObtenerRutaMapper;
import com.xpedia.backend.domain.model.enums.EstadoRuta;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.enums.ValidacionRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.service.ruta.RutaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerRutaUseCaseTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final String SLUG = "atencion-al-cliente";
    private static final String TITULO = "Atención al cliente";

    @Mock
    private RutaService rutaService;

    private ObtenerRutaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerRutaUseCase(rutaService, new ObtenerRutaMapper());
    }

    @Test
    @DisplayName("Obtiene la ruta del servicio por el id del request")
    void executeShouldGetRutaFromServiceById() {
        givenServiceReturnsRuta();

        obtener();

        thenServiceGotRuta();
    }

    @Test
    @DisplayName("Devuelve el detalle de la ruta mapeado")
    void executeShouldReturnMappedRuta() {
        givenServiceReturnsRuta();

        ObtenerRutaResponse response = obtener();

        thenResponseHasRuta(response);
    }

    @Test
    @DisplayName("Propaga el error de dominio del servicio sin convertirlo en éxito")
    void executeShouldPropagateDomainErrorFromService() {
        ResourceNotFoundException error = givenServiceThrows();

        assertThatThrownBy(this::obtener).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceReturnsRuta() {
        when(rutaService.obtener(RUTA_ID)).thenReturn(ruta());
    }

    private ResourceNotFoundException givenServiceThrows() {
        ResourceNotFoundException error = new ResourceNotFoundException("ruta", "id", RUTA_ID);
        when(rutaService.obtener(RUTA_ID)).thenThrow(error);
        return error;
    }

    // --- act ---
    private ObtenerRutaResponse obtener() {
        return useCase.execute(new ObtenerRutaRequest(RUTA_ID));
    }

    // --- assert ---
    private void thenServiceGotRuta() {
        verify(rutaService).obtener(RUTA_ID);
    }

    private void thenResponseHasRuta(ObtenerRutaResponse response) {
        assertThat(response.id()).isEqualTo(RUTA_ID);
        assertThat(response.slug()).isEqualTo(SLUG);
        assertThat(response.titulo()).isEqualTo(TITULO);
        assertThat(response.tipo()).isEqualTo(TipoRuta.TECNICA);
        assertThat(response.objetivo()).isEqualTo(ObjetivoRuta.ARRANCAR);
        assertThat(response.estado()).isEqualTo(EstadoRuta.PUBLICADA);
        assertThat(response.validacion()).isEqualTo(ValidacionRuta.BORRADOR_IA);
    }

    // --- helpers ---
    private Ruta ruta() {
        return Ruta.builder()
                .id(RUTA_ID)
                .slug(SLUG)
                .titulo(TITULO)
                .tipo(TipoRuta.TECNICA)
                .objetivo(ObjetivoRuta.ARRANCAR)
                .estado(EstadoRuta.PUBLICADA)
                .validacion(ValidacionRuta.BORRADOR_IA)
                .build();
    }
}
