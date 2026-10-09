package com.xpedia.backend.domain.service.hito;

import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import com.xpedia.backend.domain.model.hito.Hito;
import com.xpedia.backend.domain.repository.hito.HitoRepository;
import com.xpedia.backend.domain.service.ruta.RutaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HitoServiceTest {

    private static final UUID RUTA_ID = UUID.randomUUID();
    private static final UUID HITO_ID = UUID.randomUUID();

    @Mock
    private HitoRepository hitoRepository;

    @Mock
    private RutaService rutaService;

    @InjectMocks
    private HitoService hitoService;

    @Test
    @DisplayName("Valida que la ruta sea visible antes de consultar los hitos")
    void listarShouldValidateRutaBeforeQueryingHitos() {
        givenRepositoryHasHitos(List.of(hito()));

        listar();

        thenRutaWasValidatedBeforeQuery();
    }

    @Test
    @DisplayName("Devuelve los hitos que entrega el repositorio")
    void listarShouldReturnHitosFromRepository() {
        List<Hito> hitos = List.of(hito());
        givenRepositoryHasHitos(hitos);

        List<Hito> result = listar();

        assertThat(result).isSameAs(hitos);
    }

    @Test
    @DisplayName("No consulta hitos cuando la ruta no es visible")
    void listarShouldNotQueryHitosWhenRutaIsNotVisible() {
        givenRutaIsNotVisible();

        assertThatThrownBy(this::listar).isInstanceOf(ResourceNotFoundException.class);

        thenRepositoryWasNotQueried();
    }

    @Test
    @DisplayName("Acepta un hito que pertenece a la ruta")
    void validarPertenenciaShouldPassWhenHitoBelongsToRuta() {
        givenHitoBelongsToRuta(true);

        assertThatCode(this::validarPertenencia).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Lanza ResourceNotFoundException cuando el hito no pertenece a la ruta")
    void validarPertenenciaShouldThrowWhenHitoDoesNotBelongToRuta() {
        givenHitoBelongsToRuta(false);

        assertThatThrownBy(this::validarPertenencia).isInstanceOf(ResourceNotFoundException.class);
    }

    // --- arrange ---
    private void givenRepositoryHasHitos(List<Hito> hitos) {
        when(hitoRepository.findByRutaId(RUTA_ID)).thenReturn(hitos);
    }

    private void givenRutaIsNotVisible() {
        doThrow(new ResourceNotFoundException("ruta", "id", RUTA_ID)).when(rutaService).validarVisible(RUTA_ID);
    }

    private void givenHitoBelongsToRuta(boolean belongs) {
        when(hitoRepository.existsByIdAndRutaId(HITO_ID, RUTA_ID)).thenReturn(belongs);
    }

    // --- helpers ---
    private Hito hito() {
        return Hito.builder().id(HITO_ID).rutaId(RUTA_ID).build();
    }

    // --- act ---
    private List<Hito> listar() {
        return hitoService.listar(RUTA_ID);
    }

    private void validarPertenencia() {
        hitoService.validarPertenencia(RUTA_ID, HITO_ID);
    }

    // --- assert ---
    private void thenRutaWasValidatedBeforeQuery() {
        InOrder order = inOrder(rutaService, hitoRepository);
        order.verify(rutaService).validarVisible(RUTA_ID);
        order.verify(hitoRepository).findByRutaId(RUTA_ID);
    }

    private void thenRepositoryWasNotQueried() {
        verifyNoInteractions(hitoRepository);
    }
}
