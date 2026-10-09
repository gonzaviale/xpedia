package com.xpedia.backend.domain.service.ruta;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.enums.ObjetivoRuta;
import com.xpedia.backend.domain.model.enums.TipoRuta;
import com.xpedia.backend.domain.model.ruta.Ruta;
import com.xpedia.backend.domain.repository.ruta.RutaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RutaServiceTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final Pageable PAGEABLE = PageRequest.of(2, 5);

    @Mock
    private RutaRepository rutaRepository;

    @InjectMocks
    private RutaService service;

    @Test
    @DisplayName("Devuelve la ruta publicada global que entrega el repositorio")
    void obtenerShouldReturnRutaFromRepositoryWhenExists() {
        Ruta ruta = givenRepositoryFindsRuta();

        Ruta result = obtener();

        assertThat(result).isSameAs(ruta);
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando la ruta no existe o no es pública")
    void obtenerShouldThrowResourceNotFoundWhenRutaIsAbsent() {
        givenRepositoryFindsNoRuta();

        assertThatThrownBy(this::obtener).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Devuelve la página del repositorio con los filtros y la paginación recibidos")
    void listarShouldReturnRepositoryPageWhenFiltersAreGiven() {
        Page<Ruta> page = givenRepositoryReturnsPage(TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR);

        Page<Ruta> result = listar(TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR);

        thenResultIsPageListedWith(result, page, TipoRuta.CAMBIO_RUBRO, ObjetivoRuta.CAMBIAR);
    }

    @Test
    @DisplayName("Devuelve la página vacía del repositorio cuando no hay filtros ni resultados")
    void listarShouldReturnEmptyPageWhenThereAreNoFiltersNorResults() {
        givenRepositoryReturnsEmptyPage();

        Page<Ruta> result = listar(null, null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("No lanza error cuando la ruta es visible")
    void validarVisibleShouldNotThrowWhenRutaIsVisible() {
        givenRutaVisibility(true);

        assertThatCode(this::validarVisible).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando la ruta no es visible")
    void validarVisibleShouldThrowResourceNotFoundWhenRutaIsNotVisible() {
        givenRutaVisibility(false);

        assertThatThrownBy(this::validarVisible).isInstanceOf(ResourceNotFoundException.class);
    }

    // --- arrange ---
    private Ruta givenRepositoryFindsRuta() {
        Ruta ruta = Ruta.builder().id(RUTA_ID).build();
        when(rutaRepository.findPublicadaGlobalById(RUTA_ID)).thenReturn(Optional.of(ruta));
        return ruta;
    }

    private void givenRepositoryFindsNoRuta() {
        when(rutaRepository.findPublicadaGlobalById(RUTA_ID)).thenReturn(Optional.empty());
    }

    private Page<Ruta> givenRepositoryReturnsPage(TipoRuta tipo, ObjetivoRuta objetivo) {
        Page<Ruta> page = new PageImpl<>(List.of(Ruta.builder().id(RUTA_ID).build()), PAGEABLE, 20);
        when(rutaRepository.findPublicadasGlobales(tipo, objetivo, PAGEABLE)).thenReturn(page);
        return page;
    }

    private void givenRepositoryReturnsEmptyPage() {
        when(rutaRepository.findPublicadasGlobales(null, null, PAGEABLE)).thenReturn(Page.empty(PAGEABLE));
    }

    private void givenRutaVisibility(boolean visible) {
        when(rutaRepository.existsPublicadaGlobalById(RUTA_ID)).thenReturn(visible);
    }

    // --- act ---
    private Ruta obtener() {
        return service.obtener(RUTA_ID);
    }

    private Page<Ruta> listar(TipoRuta tipo, ObjetivoRuta objetivo) {
        return service.listar(tipo, objetivo, PAGEABLE);
    }

    private void validarVisible() {
        service.validarVisible(RUTA_ID);
    }

    // --- assert ---
    private void thenResultIsPageListedWith(Page<Ruta> result, Page<Ruta> expected, TipoRuta tipo,
                                            ObjetivoRuta objetivo) {
        assertThat(result).isSameAs(expected);
        verify(rutaRepository).findPublicadasGlobales(tipo, objetivo, PAGEABLE);
    }
}
