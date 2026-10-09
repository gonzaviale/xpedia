package com.xpedia.backend.domain.service.nodo;

import com.xpedia.backend.domain.model.nodo.Nodo;
import com.xpedia.backend.domain.repository.nodo.NodoRepository;
import com.xpedia.backend.domain.service.nodo.NodoService;
import com.xpedia.backend.domain.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;
import com.xpedia.backend.domain.service.ruta.RutaService;
import com.xpedia.backend.domain.service.hito.HitoService;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.xpedia.backend.support.ContractData.*;

class NodoServiceTest {
    private final NodoRepository repository = mock(NodoRepository.class);
    private final RutaService rutas = mock(RutaService.class);
    private final HitoService hitos = mock(HitoService.class);
    private final NodoService service = new NodoService(repository, rutas, hitos);
    @Test void sinFiltroNoValidaHito() {
        var ruta = UUID.randomUUID(); var result = List.of(full(Nodo.class));
        when(repository.findByRutaId(ruta, null)).thenReturn(result);
        assertThat(service.listar(ruta, null)).isSameAs(result); verifyNoInteractions(hitos);
        verify(rutas).obtener(ruta);
    }
    @Test void conFiltroValidaPertenenciaAntesDeConsultar() {
        var ruta = UUID.randomUUID(); var hito = UUID.randomUUID();
        when(repository.findByRutaId(ruta, hito)).thenReturn(List.of());
        assertThat(service.listar(ruta, hito)).isEmpty();
        var order = inOrder(rutas, hitos, repository); order.verify(rutas).obtener(ruta);
        order.verify(hitos).validarPertenencia(ruta, hito); order.verify(repository).findByRutaId(ruta, hito);
    }
    @Test void rutaOcultaImpideConsultarNodosYHitos() {
        var ruta = UUID.randomUUID(); when(rutas.obtener(ruta)).thenThrow(new ResourceNotFoundException("ruta", "id", ruta));
        assertThatThrownBy(() -> service.listar(ruta, null)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(repository, hitos);
    }
    @Test void hitoAjenoImpideConsultarNodos() {
        var ruta = UUID.randomUUID(); var hito = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("hito", "id", hito)).when(hitos).validarPertenencia(ruta, hito);
        assertThatThrownBy(() -> service.listar(ruta, hito)).isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(repository);
    }
}
