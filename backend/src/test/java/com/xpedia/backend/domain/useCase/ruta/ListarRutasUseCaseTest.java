package com.xpedia.backend.domain.useCase.ruta;

import com.xpedia.backend.domain.dto.ruta.ListarRutasRequest;
import com.xpedia.backend.domain.dto.ruta.ListarRutasResponse;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.mapper.ruta.ListarRutasMapper;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.service.ruta.RutaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarRutasUseCaseTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final String SLUG = "atencion-al-cliente";
    private static final String TITULO = "Atención al cliente";
    private static final int PAGE = 1;
    private static final int SIZE = 5;
    private static final long TOTAL_ELEMENTS = 10;
    private static final Pageable PAGEABLE = PageRequest.of(PAGE, SIZE, Sort.by("titulo", "id"));

    @Mock
    private RutaService rutaService;

    private ListarRutasUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarRutasUseCase(rutaService, new ListarRutasMapper());
    }

    @Test
    @DisplayName("Delega al servicio los filtros y la paginación ordenada por título e id")
    void executeShouldDelegateFiltersAndSortedPageableToService() {
        givenServiceReturnsPage(TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR);

        listar(TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR);

        thenServiceListedWith(TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR);
    }

    @Test
    @DisplayName("Devuelve la página mapeada con sus metadatos y los datos de la ruta")
    void executeShouldReturnMappedPage() {
        givenServiceReturnsPage(null, null);

        ListarRutasResponse response = listar(null, null);

        thenResponseHasMappedPage(response);
    }

    @Test
    @DisplayName("Propaga el error de dominio del servicio sin convertirlo en éxito")
    void executeShouldPropagateDomainErrorFromService() {
        ResourceNotFoundException error = givenServiceThrows();

        assertThatThrownBy(() -> listar(null, null)).isSameAs(error);
    }

    // --- arrange ---
    private void givenServiceReturnsPage(TipoRuta tipo, ObjetivoRuta objetivo) {
        when(rutaService.listar(tipo, objetivo, PAGEABLE)).thenReturn(pageWithRuta());
    }

    private ResourceNotFoundException givenServiceThrows() {
        ResourceNotFoundException error = new ResourceNotFoundException("ruta", "id", RUTA_ID);
        when(rutaService.listar(null, null, PAGEABLE)).thenThrow(error);
        return error;
    }

    // --- act ---
    private ListarRutasResponse listar(TipoRuta tipo, ObjetivoRuta objetivo) {
        return useCase.execute(new ListarRutasRequest(tipo, objetivo, PAGE, SIZE));
    }

    // --- assert ---
    private void thenServiceListedWith(TipoRuta tipo, ObjetivoRuta objetivo) {
        verify(rutaService).listar(tipo, objetivo, PAGEABLE);
    }

    private void thenResponseHasMappedPage(ListarRutasResponse response) {
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().id()).isEqualTo(RUTA_ID);
        assertThat(response.content().getFirst().slug()).isEqualTo(SLUG);
        assertThat(response.content().getFirst().titulo()).isEqualTo(TITULO);
        assertThat(response.pageNumber()).isEqualTo(PAGE);
        assertThat(response.pageSize()).isEqualTo(SIZE);
        assertThat(response.totalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(response.totalPages()).isEqualTo(2);
        assertThat(response.first()).isFalse();
        assertThat(response.last()).isTrue();
    }

    // --- helpers ---
    private Page<Ruta> pageWithRuta() {
        return new PageImpl<>(List.of(ruta()), PageRequest.of(PAGE, SIZE), TOTAL_ELEMENTS);
    }

    private Ruta ruta() {
        return Ruta.builder()
                .id(RUTA_ID)
                .slug(SLUG)
                .titulo(TITULO)
                .build();
    }
}
